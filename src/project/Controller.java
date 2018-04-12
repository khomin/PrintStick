package project;

import com.sun.org.apache.xerces.internal.xs.StringList;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;

public class Controller {
    @FXML
    public CheckBox ch_print_mode_select;
    @FXML
    public Button bt_print_auto;
    @FXML
    public MenuItem menu_settings_db;
    @FXML
    public MenuItem menu_settings_scanner;
    @FXML
    public TextArea log_text_field;
    //
    BooleanProperty stiker_ready;
    //
    Db data_base = new Db();
    Stage stage_settings = new Stage();
    Stage stage_scanner = new Stage();

    public void initialize() {
        stiker_ready = new SimpleBooleanProperty(true);
        bt_print_auto.disableProperty().bind(stiker_ready);
        //
        try {
            FXMLLoader fxmlLoader_conf_db = new FXMLLoader();
            fxmlLoader_conf_db.setLocation(getClass().getResource("/project/dbForm.fxml"));
            //
            FXMLLoader fxmlLoader_conf_tty = new FXMLLoader();
            fxmlLoader_conf_tty.setLocation(getClass().getResource("/project/Scanner.fxml"));

            // настройка бд
            Scene sceneDb = new Scene(fxmlLoader_conf_db.load());
            stage_settings.setScene(sceneDb);
            data_base = (Db) fxmlLoader_conf_db.getController();
            stage_settings.initModality(Modality.APPLICATION_MODAL);
            stage_settings.setTitle("Настройка БД");
            // настройка порта
            Scene sceneComPortSettings = new Scene(fxmlLoader_conf_tty.load());
            stage_scanner.setScene(sceneComPortSettings);
            Scanner scanner = (Scanner)fxmlLoader_conf_tty.getController();
            scanner.startScanner();
            stage_scanner.initModality(Modality.APPLICATION_MODAL);
            stage_scanner.setTitle("Настройка порта");

            ActionListener stikerStateListener = new TimerCheckSticker(scanner);
            javax.swing.Timer timer_sticker = new javax.swing.Timer(1000, stikerStateListener);
            timer_sticker.start();
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

    public class TimerCheckSticker implements ActionListener {
        Scanner scanner = null;
        public TimerCheckSticker(Scanner inScanner) {
            this.scanner = inScanner;
        }
        public void actionPerformed(ActionEvent event) {
            ArrayList<String> result = scanner.stickMaker.availablePrintSticker();
            if(result.size() != 0) {
                log_text_field.setText(null);
                for(int counter = 0; counter < result.size(); counter++) {
                    log_text_field.insertText(0, result.get(counter).toString());
                    stiker_ready.set(false);
                    counter++;
                }
            }
        }
    }
}
