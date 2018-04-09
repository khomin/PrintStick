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
import java.util.ArrayList;

public class db {
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
    String name;
    String host;
    String passwd;
    XmlSettings settings;
    String xml = "./db_settings.xml";

    public void initialize() {
        //-- settings
        settings = new XmlSettings();
        ArrayList<String>data_settings = settings.readXML_dataBase(xml);
        if(!data_settings.isEmpty()) {
            name = data_settings.get(0);
            host = data_settings.get(1);
            passwd = data_settings.get(2);
            port = data_settings.get(3);
        } else {
            settings.saveToXML_dataBase("postgres","localhost","5432","1qaz@WSX3edc$RFV",xml);
        }
        //
        button_accept.disableProperty().bind(
                text_field_name.textProperty().isEmpty()
                        .or(text_field_host.textProperty().isEmpty())
                        .or(text_field_port.textProperty().isEmpty())
                        .or(text_field_passwd.textProperty().isEmpty())
        );
        StringProperty name_property = new SimpleStringProperty(name);
        StringProperty host_property = new SimpleStringProperty(host);
        StringProperty port_property = new SimpleStringProperty(port);
        StringProperty passwd_property = new SimpleStringProperty(passwd);
        text_field_name.textProperty().bindBidirectional(name_property);
        text_field_port.textProperty().bindBidirectional(port_property);
        text_field_host.textProperty().bindBidirectional(host_property);
        text_field_passwd.textProperty().bindBidirectional(passwd_property);
    }
    public void onExit() {
        Stage stage;
        stage = (Stage)button_accept.getScene().getWindow();
        stage.close();
    }
    String port;
    public void onAccept() {
        settings.saveToXML_dataBase(name, host,port,passwd, xml);
        Stage stage;
        stage = (Stage)button_accept.getScene().getWindow();
        stage.close();
    }
}
