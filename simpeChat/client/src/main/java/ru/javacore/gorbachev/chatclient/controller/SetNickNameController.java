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
    private TextField textFieldLogin;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private AnchorPane setNickNameLayout;

    private final SettingsXML settingsXML;
    private final FileConfig fileConfig;

    public SetNickNameController() {
        this.fileConfig = new FileConfig();
        this.settingsXML = this.fileConfig.loadXML();
    }

    //Метод вызываемый при отрисовки формы
    @FXML
    public void initialize() {
        this.textFieldUser.requestFocus();
        if (!"None".equals(this.settingsXML.getUser())) {
            this.textFieldUser.setText(settingsXML.getUser());
        }
        if (!"None".equals(this.settingsXML.getUserName())) {
            this.textFieldLogin.setText(settingsXML.getUserName());
        }
        if (!"None".equals(this.settingsXML.getPassword())) {
            this.textFieldPassword.setText(settingsXML.getPassword());
        }
    }

    public void setNickNameStage(Stage mainStage) {
        this.setNickNameStage = mainStage;
    }

    //Обработка кнопки Принять
    public void clickOK(ActionEvent actionEvent) {
        this.fileConfig.createXML("None", textFieldUser.getText(),
                this.textFieldLogin.getText(), this.textFieldPassword.getText());
        onExit(actionEvent);
    }

    //Выход
    public void onExit(ActionEvent actionEvent) {
        Node source = (Node)actionEvent.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }
}
