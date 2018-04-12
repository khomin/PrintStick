package project;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import javax.script.Bindings;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class Db {

    private static Db instance;

    public static synchronized Db getInstance() {
        if (instance == null) {
            instance = new Db();
        }
        return instance;
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
    StringProperty name = new SimpleStringProperty();
    StringProperty host = new SimpleStringProperty();
    StringProperty port = new SimpleStringProperty();
    StringProperty passwd = new SimpleStringProperty();

    XmlSettings settings;
    String xml = "./db_settings.xml";
    Connection db_connection = null;

    public Db() {
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
            System.out.println("Opened database successfully");
        } catch (SQLException ex) {
            System.err.print(ex);
        }
    }

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
}
