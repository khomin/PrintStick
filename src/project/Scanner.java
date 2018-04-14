package project;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jssc.*;

import java.io.*;
import java.net.PortUnreachableException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;

public class Scanner {
    @FXML
    public ComboBox comPortList;
    @FXML
    public Button button_accept;
    @FXML
    public Button button_exit;
    public SerialPort com_port;
    private String com_port_name = "";
    private InputStream in_stream;
    private OutputStream out_stream;
    public StickMaker stickMaker = new StickMaker();
    public BooleanProperty auto_print_mode = new SimpleBooleanProperty(true);
    final private String port_scanner_man = "text_man";
    //
    XmlSettings settings;
    String xml = "./tty_settings.xml";

    @FXML
    public void initialize() {
        //-- settings
        settings = new XmlSettings();
        com_port_name = settings.readXML_tty(xml);
        if(com_port_name.isEmpty()) {
            settings.saveToXML_tty("COM1",xml);
            com_port_name = "COM1";
        }
        String [] portNames = SerialPortList.getPortNames();
        for(int i = 0; i < portNames.length; i++){
            System.out.println(portNames[i]);
        }
        try {
            comPortList.setItems(FXCollections.observableArrayList(portNames));
            if(!com_port_name.isEmpty()) {
                comPortList.setValue(com_port_name);
            } else {
                comPortList.setValue(portNames[0].toString());
            }
            connect(com_port_name);
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка открытия порта " + com_port_name + "\nВыберите другой порт!");
            alert.showAndWait();
            System.err.print(ex);
        }
    }

    public void  disconnect() {
        try {
            com_port.closePort();
        } catch (SerialPortException ex) {
            System.err.print(ex);
        }
    }

    public boolean connect(String port) throws Exception {
        boolean res = false;
        try {
            if(com_port_name.isEmpty()) { //-- если в настройках нет порта
                com_port_name = comPortList.getSelectionModel().getSelectedItem().toString();
            }
            com_port = new SerialPort(com_port_name);
            if(com_port_name.length() != 0) {
                res = com_port.openPort();
                com_port.setParams(SerialPort.BAUDRATE_115200,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);
            }
        } catch (SerialPortException ex) {
            System.err.print(ex.getMessage());
            Stage stage = (Stage)button_accept.getScene().getWindow();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setTitle("Ошибка");
            alert.setContentText("Ошибка открытия порта");
            alert.show();
        }
        return res;
    }

    public void startScanner() {
        int mask = SerialPort.MASK_RXCHAR;
        try {
            com_port.addEventListener(new SerialPortReader(com_port, stickMaker, auto_print_mode));
        } catch(SerialPortException ex ) {
            System.err.print(ex.getMessage());
        }
    }

    @FXML
    public void onAccept() {
        Stage stage;
        settings.saveToXML_tty(comPortList.getSelectionModel().getSelectedItem().toString(), xml);
        stage = (Stage)button_accept.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void onExit() {
        Stage stage;
        stage = (Stage)button_accept.getScene().getWindow();
        stage.close();
    }

    static class SerialPortReader implements SerialPortEventListener {
        StickMaker stickMaker = null;
        SerialPort com_threa_port;
        BooleanProperty auto_print_mode;
        public SerialPortReader(SerialPort port, StickMaker instickMaker,
                                BooleanProperty auto_print_mode) {
            com_threa_port = port;
            stickMaker = instickMaker;
            this.auto_print_mode = auto_print_mode;
        }
        public void serialEvent(SerialPortEvent event) {
            byte [] buffer = {0};
            String qr_code = "";
            try {
                buffer = com_threa_port.readBytes();
                qr_code = new String(buffer, "UTF-8");
                System.out.print(qr_code);
                stickMaker.getStickBoxed(qr_code, auto_print_mode.get());
            }
            catch (SerialPortException ex) {
                System.out.println(ex);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
