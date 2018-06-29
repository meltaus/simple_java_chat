package ru.javacore.gorbachev.chatclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.javacore.gorbachev.chatclient.controller.MainWindowChatClientController;

import java.util.Locale;
import java.util.ResourceBundle;

public class ChatClientMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        //Инициализация главного окна, макета и контроллера
        String fxmlFile = "/fxml/MainWindowChatClient.fxml";
        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setResources(ResourceBundle.getBundle("ru.javacore.gorbachev.chatclient.bundles.Locale", new Locale("ru")));
        Parent fxmlMain = (Parent) fxmlLoader.load(getClass().getResourceAsStream(fxmlFile));
        MainWindowChatClientController mainWindowChatClientController = fxmlLoader.getController();
        mainWindowChatClientController.setMainStage(primaryStage);

        //Минимальный размер окна
        primaryStage.setMinHeight(450);
        primaryStage.setMinWidth(600);
        primaryStage.setScene(new Scene(fxmlMain, 600, 450));
        primaryStage.setTitle("Простой чат на Java");
        primaryStage.show();
    }
}
