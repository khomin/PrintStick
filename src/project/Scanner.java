package project;

import gnu.io.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.PortUnreachableException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;

public class Scanner {
    @FXML
    public ComboBox comPortList;
    @FXML
    public Button button_accept;
    @FXML
    public Button button_exit;

    SerialPort com_port;
    private String com_port_name = "";
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
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        ArrayList<CommPortIdentifier> comPort = new ArrayList<>();
        ArrayList<String> comPortNames = new ArrayList<>();
        while(portEnum.hasMoreElements()) {
            comPort.add((CommPortIdentifier)portEnum.nextElement());
        }
        for(int i=0; i<comPort.size(); i++) {
            comPortNames.add(comPort.get(i).getName());
        }
        try {
            comPortList.setItems(FXCollections.observableArrayList(comPortNames));
            if(!com_port_name.isEmpty()) {
                comPortList.setValue(com_port_name);
            } else {
                comPortList.setValue(comPortNames.get(0).toString());
            }
            connect(com_port_name);
        } catch (Exception ex){
            System.err.print(ex);
        }
    }

    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(com_port_name,2000);

            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

                InputStream in = serialPort.getInputStream();
                OutputStream out = serialPort.getOutputStream();

                (new Thread(new SerialReader(in))).start();
                (new Thread(new SerialWriter(out))).start();

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

//    public boolean openScannerPort() throws Exception {
//        boolean res = false;
//        try {
//            if(com_port_name.isEmpty()) { //-- если в настройках нет порта
//                com_port_name = comPortList.getSelectionModel().getSelectedItem().toString();
//            }
//            if(com_port_name.length() != 0) {
////                CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(com_port_name);
////                SerialPort com_port = (SerialPort) portId.open(com_port_name, 1000);
////                com_port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
////                InputStream in_stream = com_port.getInputStream();
////                OutputStream out_stream = com_port.getOutputStream();
////                (new Thread(new SerialReader(in_stream))).start();
////                (new Thread(new SerialWriter(out_stream))).start();
//            }
//            Stage stage = (Stage) comPortList.getScene().getWindow();
//            stage.hide();
//        } catch (PortInUseException ex) {
//            System.err.print(ex.getMessage());
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Ошибка");
//            alert.setContentText("Ошибка открытия порта");
//            alert.show();
//        }
//        return res;
//    }

    /** */
    public static class SerialReader implements Runnable {
        InputStream in;
        public SerialReader ( InputStream in ) {
            this.in = in;
        }
        public void run () {
            byte[] buffer = new byte[1024];
            int len = -1;
            try {
                while ( ( len = this.in.read(buffer)) > -1 ) {
                    System.out.print(new String(buffer,0,len));
                }
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }
    /** */
    public static class SerialWriter implements Runnable {
        OutputStream out;
        public SerialWriter ( OutputStream out ) {
            this.out = out;
        }
        public void run () {
            try {
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }
            }
            catch(IOException e ) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onAccept() {
        Stage stage;
        stage = (Stage)button_accept.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void onExit() {
        Stage stage;
        stage = (Stage)button_accept.getScene().getWindow();
        stage.close();
    }
}
