package project;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import jdk.nashorn.internal.parser.JSONParser;
import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;
import org.w3c.dom.Text;
import org.postgresql.util.*;
import org.json.*;
import javax.script.Bindings;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class Db {
    private static Db singleton = new Db ( );

    /* Static 'instance' method */
    public static Db getInstance( ) {
        return singleton;
    }

    /* Other methods protected by singleton-ness */
    protected static void demoMethod( ) {
        System.out.println("demoMethod for singleton");
    }

    @FXML
    javafx.scene.control.TextField text_field_name;
    @FXML
    javafx.scene.control.TextField text_field_host;
    @FXML
    javafx.scene.control.TextField text_field_port;
    @FXML
    javafx.scene.control.TextField text_field_passwd;
    @FXML
    javafx.scene.control.Button button_accept;
    @FXML
    javafx.scene.control.Button button_exit;
    //
    public static BooleanProperty db_no_ready = new SimpleBooleanProperty(true);
    public static StringProperty name = new SimpleStringProperty();
    public static StringProperty host = new SimpleStringProperty();
    public static StringProperty port = new SimpleStringProperty();
    public static StringProperty passwd = new SimpleStringProperty();

    private static XmlSettings settings;
    private static String xml = "./db_settings.xml";
    private static Connection db_connection;

    public void initialize() {
        //-- form
        button_accept.disableProperty().bind(
                text_field_name.textProperty().isEmpty()
                        .or(text_field_host.textProperty().isEmpty())
                        .or(text_field_port.textProperty().isEmpty())
                        .or(text_field_passwd.textProperty().isEmpty())
        );
        text_field_name.textProperty().bindBidirectional(name);
        text_field_port.textProperty().bindBidirectional(port);
        text_field_host.textProperty().bindBidirectional(host);
        text_field_passwd.textProperty().bindBidirectional(passwd);
        //-- settings
        settings = new XmlSettings();
        ArrayList<String>data_settings = settings.readXML_dataBase(xml);
        if(!data_settings.isEmpty()) {
            name.set(data_settings.get(0));
            host.set(data_settings.get(1));
            passwd.set(data_settings.get(2));
            port.set(data_settings.get(3));
        } else {
            settings.saveToXML_dataBase("postgres","localhost","5432","1qaz@WSX3edc$RFV",xml);
        }
        try {
            db_connection = DriverManager.getConnection("jdbc:postgresql://"
                    + host.get() +":" + port.get() + "/" + name.get(),"postgres", passwd.get());
            db_no_ready.set(false);
            System.out.println("Opened database successfully");
        } catch (SQLException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка открытия БД\nПроверьте соединение с сетью и перезапустите программу!");
            alert.showAndWait();
            db_no_ready.set(true);
            System.err.print(ex);
        }
    }
    public void onExit() {
        Stage stage;
        stage = (Stage)button_accept.getScene().getWindow();
        stage.close();
    }
    public void onAccept() {
        settings.saveToXML_dataBase(name.get(), host.get(), port.get(), passwd.get(), xml);
        Stage stage;
        stage = (Stage)button_accept.getScene().getWindow();
        stage.close();
    }

    public boolean checkDeviceQrCode(String qr_code) {
        boolean res = false;
        try {
            PreparedStatement st = (PreparedStatement)db_connection.prepareStatement("SELECT pr_check_device_qr_code(?, ?, ?, ?);");
            String qr_code_items[] = qr_code.split("#");
            String prefix, dts, dev_serial, client;
            prefix = qr_code_items[0];
            client = qr_code_items[1];
            dts = qr_code_items[2];
            dev_serial = qr_code_items[3];
            dev_serial = dev_serial.replaceAll("\r", "");
            dev_serial = dev_serial.replaceAll("\n", "");
            //
            st.setString(1, prefix);
            st.setString(2, client);
            st.setString(3, dts);
            st.setString(4, dev_serial);
            ResultSet rs = st.executeQuery();
            rs.next();
            res = rs.getBoolean("pr_check_device_qr_code");

        } catch (SQLException ex) {
            System.err.print(ex);
        }
        return res;
    }

    public StickerAttributes getStickers(String qr_code) {
        StickerAttributes attributes = new StickerAttributes();
        try {
            PreparedStatement ps = db_connection.prepareStatement("SELECT f_dts_type, f_data FROM public.file WHERE f_id=?");
            ps.setInt(1, 7);
            ResultSet rs = ps.executeQuery();
            rs.next();
            JSONObject jsonObject = new JSONObject(rs.getString(1));
            attributes.bytea = rs.getBytes(2);
            // TODO: db!!!
            attributes.serial_number = "10000001";
            //
            attributes.qr_code = qr_code;
            attributes.print_qr = jsonObject.getBoolean("print_qr");
            attributes.print_serial_num = jsonObject.getBoolean("print_serial_num");
            attributes.serial_number_font_size = jsonObject.getInt("serial_number_font_size");
            attributes.serial_number_x = jsonObject.getInt("serial_number_x");
            attributes.serial_number_y = jsonObject.getInt("serial_number_y");
            attributes.print_barcode = jsonObject.getBoolean("print_barcode");
            attributes.barcode_code = jsonObject.getString("barcode_code");
            attributes.barcode_rote = jsonObject.getBoolean("barcode_rote");
            attributes.barcode_height = jsonObject.getInt("barcode_height");
            attributes.barcode_widht = jsonObject.getInt("barcode_widht");
            attributes.point_barcode_x = jsonObject.getInt("point_barcode_x");
            attributes.point_barcode_y = jsonObject.getInt("point_barcode_y");
            attributes.qr_height = jsonObject.getInt("qr_height");
            attributes.qr_widht = jsonObject.getInt("qr_widht");
            attributes.point_qr_x = jsonObject.getInt("point_qr_x");
            attributes.point_qr_y = jsonObject.getInt("point_qr_y");
            //
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return attributes;
    }

//    public StickerAttributes getStickers(String qr_code) {
//        StickerAttributes attributes = new StickerAttributes();
//        try {
//            File file = new File("E:\\PROJECTs\\Print_stick\\stikers\\raw\\sticker_progress.png");
//            FileInputStream fis = new FileInputStream(file);
//            PreparedStatement ps = db_connection.prepareStatement("INSERT INTO public.file(f_data) VALUES (?)");
////            ps.setString(1, file.getName());
//            ps.setBinaryStream(1, fis, (int)file.length());
//            ps.executeUpdate();
//            ps.close();
//            fis.close();
//            db_connection.commit();
//        } catch (SQLException ex) {
//            System.err.print(ex);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return attributes;
//    }
}
