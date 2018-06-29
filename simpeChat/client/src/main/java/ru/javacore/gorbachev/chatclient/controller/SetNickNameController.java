package ru.javacore.gorbachev.chatclient.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.javacore.gorbachev.chatclient.essence.SettingsXML;
import ru.javacore.gorbachev.chatclient.login.FileConfig;

public class SetNickNameController {

    private Stage setNickNameStage;

    @FXML
    private TextField textFieldUser;
    @FXML
    private AnchorPane setNickNameLayout;

    //Метод вызываемый при отрисовки формы
    @FXML
    public void initialize() {
        this.textFieldUser.requestFocus();
        FileConfig fileConfig = new FileConfig();
        SettingsXML settingsXML = new SettingsXML();
        settingsXML = fileConfig.loadXML();
        if (!settingsXML.getUser().equals("None")) {
            this.textFieldUser.setText(settingsXML.getUser());
        }
    }

    public void setNickNameStage(Stage mainStage) {
        this.setNickNameStage = mainStage;
    }

    //Обработка кнопки Принять
    public void clickOK(ActionEvent actionEvent) {
        FileConfig fileConfig = new FileConfig();
        SettingsXML settingsXML = new SettingsXML();
        settingsXML = fileConfig.loadXML();
        fileConfig.createXML("None", textFieldUser.getText());
        onExit(actionEvent);
    }

    //Выход
    public void onExit(ActionEvent actionEvent) {
        Node source = (Node)actionEvent.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }
}
