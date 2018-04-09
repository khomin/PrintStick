package project;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.events.Event;
import javax.xml.*;

import java.awt.*;

public class Controller {
    @FXML
    public CheckBox ch_print_mode_select;
    @FXML
    public Button bt_print_auto;
    @FXML
    public MenuItem menu_settings_db;
    @FXML
    public MenuItem menu_settings_scanner;
    //
    BooleanProperty print_mode_auto;
    //
    db data_base = new db();
    Stage stage_settings = new Stage();
    Stage stage_scanner = new Stage();
    Scanner scanner = new Scanner();

    public void initialize() {
        print_mode_auto = new SimpleBooleanProperty(false);
        bt_print_auto.disableProperty().bind(print_mode_auto);
        ch_print_mode_select.setOnAction(Event-> {
            print_mode_auto.set(!print_mode_auto.get());
        });

        FXMLLoader fxmlLoader_conf_db = new FXMLLoader();
        fxmlLoader_conf_db.setLocation(getClass().getResource("/project/dbForm.fxml"));
        //
        FXMLLoader fxmlLoader_conf_tty = new FXMLLoader();
        fxmlLoader_conf_tty.setLocation(getClass().getResource("/project/Scanner.fxml"));
        //
        try {
            // настройка бд
            Scene sceneDb = new Scene(fxmlLoader_conf_db.load());
            stage_settings.setScene(sceneDb);
            data_base = (db) fxmlLoader_conf_db.getController();
            stage_settings.initModality(Modality.APPLICATION_MODAL);
            stage_settings.setTitle("Настройка БД");
            // настройка порта
            Scene sceneComPortSettings = new Scene(fxmlLoader_conf_tty.load());
            stage_scanner.setScene(sceneComPortSettings);
            data_base = (db) fxmlLoader_conf_tty.getController();
            stage_scanner.initModality(Modality.APPLICATION_MODAL);
            stage_scanner.setTitle("Настройка порта");
        } catch (Exception ex) {
            System.err.printf(ex.toString());
        }
    }
    public void onEditSettingsDbTrig(){
        stage_settings.show();
    }
    public void onEditScannerTrig(){
        stage_scanner.show();
    }
    public void onPrintTrig() {

    }
}
