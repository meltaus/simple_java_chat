package ru.javacore.gorbachev.chatclient.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.awt.*;

public class AboutController {

    private Stage aboutStage;

    //Метод вызываемый при отрисовки формы
    @FXML
    public void initialize() {
    }

    public void setAboutStage(Stage mainStage) {
        this.aboutStage = mainStage;
    }

    //Выход
    public void onExit(ActionEvent actionEvent) {
        Node source = (Node)actionEvent.getSource();
        Stage stage = (Stage)source.getScene().getWindow();
        stage.close();
    }
}
