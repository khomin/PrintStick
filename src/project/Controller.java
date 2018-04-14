package project;

import com.sun.org.apache.xerces.internal.xs.StringList;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.Timer;

public class Controller {
    @FXML
    public CheckBox ch_print_mode_select;
    @FXML
    public Button bt_print_auto;
    @FXML
    public SplitPane plane;
    @FXML
    public MenuItem menu_settings_db;
    @FXML
    public MenuItem menu_settings_scanner;
    @FXML
    public MenuItem menu_info;
    @FXML
    public MenuItem menu_close;
    @FXML
    public TextArea log_text_field;
    //
    private BooleanProperty stiker_no_ready;
    //
    Db data_base = Db.getInstance();
    Stage stage_settings = new Stage();
    Stage stage_scanner = new Stage();
    Scanner scanner;

    public void initialize() {
        stiker_no_ready = new SimpleBooleanProperty(true);
        bt_print_auto.disableProperty().bind(stiker_no_ready);
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
            plane.disableProperty().bind(data_base.db_no_ready);
            stage_settings.setTitle("Настройка БД");
            // настройка порта
            Scene sceneComPortSettings = new Scene(fxmlLoader_conf_tty.load());
            stage_scanner.setScene(sceneComPortSettings);
            scanner = (Scanner)fxmlLoader_conf_tty.getController();
            scanner.startScanner();
            ch_print_mode_select.selectedProperty().bindBidirectional(scanner.auto_print_mode);
            stage_scanner.initModality(Modality.APPLICATION_MODAL);
            stage_scanner.setTitle("Настройка порта");
            //
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
    public void onCloseTrig() {
        Stage stage = (Stage) bt_print_auto.getScene().getWindow();
        System.exit(0);
    }
    public void onInfoTrig() {
        Stage stage = (Stage)plane.getScene().getWindow();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setTitle("О программе");
        alert.setHeaderText("");
        alert.setContentText("Программа для печати наклеек\nВерсия 0.0\nЛокус 2018 год");
        alert.show();
    }
    public void onPrintTrig() {
        scanner.stickMaker.getStickBoxed(null, true);
    }

    public class TimerCheckSticker implements ActionListener {
        Scanner scanner = null;
        public TimerCheckSticker(Scanner inScanner) {
            this.scanner = inScanner;
        }
        public void actionPerformed(ActionEvent event) {
            ArrayList<String> result = scanner.stickMaker.getPrintStickerInformation();
            log_text_field.setText(null);
            if(result.size() != 0) {
                for(int counter = 0; counter < result.size(); counter++) {
                    log_text_field.insertText(0, result.get(counter).toString());
                    counter++;
                }
            }
            if(scanner.stickMaker.availablePrintSticker()) {
                stiker_no_ready.set(false);
            } else {
                stiker_no_ready.set(true);
            }
        }

    }
}
