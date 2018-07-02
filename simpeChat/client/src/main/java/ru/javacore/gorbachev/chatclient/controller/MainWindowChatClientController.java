package ru.javacore.gorbachev.chatclient.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ru.javacore.gorbachev.chatclient.essence.SettingsXML;
import ru.javacore.gorbachev.chatclient.login.FileConfig;
import ru.javacore.gorbachev.chatclient.network.connection.TCPConnection;
import ru.javacore.gorbachev.chatclient.network.listener.TCPConnectionListener;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class MainWindowChatClientController implements Initializable, TCPConnectionListener {

    private static final String DEFAULT_USER_NAME = "default";
    private static final int TIME_ON_AUTH = 120;

    //Текущие настройки приложения. Может перечитываться в процессе работы программы и обновляет параметры
    private SettingsXML settingsXML;

    //Элементы с макета
    @FXML
    private TextArea mainChatArea;
    @FXML
    private TextField mainUserTextField;
    @FXML
    private ListView nickNameColumn;
    @FXML
    private Button btnSendMsg;
    @FXML
    private MenuItem setNickName;
    @FXML
    private Label labelStatus;
    @FXML
    private MenuItem about;

    private Stage mainStage;

    //Контроллеры
    private SetNickNameController setNickNameController;
    private AboutController aboutController;

    //Параметры для соединения
    private String ipAddres;
    private int port;
    private TCPConnection connection;
    private String userName = DEFAULT_USER_NAME;

    //Список всех пользователей чата
    private ArrayList<String> nickList = new ArrayList<>();

    //Ресурсы перевода
    private ResourceBundle resourceBundle;

    //Состояние авторизации
    private Boolean isAuth;

    public MainWindowChatClientController() {
        this.ipAddres = "localhost";
        this.port = 8189;
        this.isAuth = false;
    }

    //Метод, выполняемый при инициализации макета
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        //Запрет редактирования в главном окне чата
        this.mainChatArea.setEditable(false);
        //Инициализация слушателей
        initListeners();
        //Инициализируем подключение к серверу
        try {
            connection = new TCPConnection(this, this.ipAddres, this.port);
        } catch (IOException e) {
            onExeption(connection, e);
        }
        //Загружаем конфигурацию
        loadConfigure();
        //Проверяем задан ли ник
        if (DEFAULT_USER_NAME.equals(this.userName)) {
            loadDialog("setNickName");
        }
        long currentTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while ((TIME_ON_AUTH * 1000) >= (System.currentTimeMillis() - currentTime)) {
                    if (isAuth) {
                        printMsg("Вы авторизованны успешно");
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (!isAuth) {
                    printMsg("Время на авторизацию закончено");
                    connection.disconnect();
                }
            }
        }).start();
    }

    //Слушатели
    private void initListeners() {
        //Обработка отправки сообщения по кнопке Enter
        this.mainUserTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    actionMainUserTextField();
                }
            }
        });
        //обработка двойного клика по нику
        this.nickNameColumn.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    String nick = (String) nickNameColumn.getSelectionModel().selectedItemProperty().getValue();
                    Platform.runLater(() -> {
                        mainUserTextField.setText("/w " + nick);
                    });
                }
            }
        });
    }

    private void actionMainUserTextField() {
        if (!this.mainUserTextField.getText().equals("")) {
            this.connection.sendMsg(this.userName + ": " + this.mainUserTextField.getText());
            this.mainUserTextField.setText("");
        } else {
            System.out.println("Пустое сообщение");
        }
    }

    public void pressBtnSendMsg() {
        actionMainUserTextField();
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    //Выход
    public void onExit() {
        System.exit(0);
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMsg("Соединение с сервером установленно");
    }

    @Override
    public void onReciveMsg(TCPConnection tcpConnection, String value) {
        if (value.contains("/AllNicks ")) {
            System.out.println("Все ники");
            value = value.replace("/AllNicks ", "");
            String[] strNick = value.split(" - ");
            this.nickList = null;
            this.nickList = new ArrayList<>();
            int count = strNick.length;
            for (int i = 0; i < count; i++) {
                this.nickList.add(strNick[i].replace(" ", ""));
            }
            updateNickNameColumn();
        } else if(value.contains("/AuthUser")) {
            if (value.contains("true")) {
                this.isAuth = true;
            }
            if (value.contains("false")) {
                printMsg("Авторизация отклонена");
                this.connection.disconnect();
            }
        } else {
            if (this.isAuth) {
                printMsg(value);
            }
        }
    }

    @Override
    public void onDisconect(TCPConnection tcpConnection) {
        printMsg("Соединение потеряно");
    }

    @Override
    public void onExeption(TCPConnection tcpConnection, Exception e) {
        printMsg("Ошибка подключения к серверу " + e);
    }

    private synchronized void printMsg(String msg) {
        Platform.runLater(() -> {
            mainChatArea.appendText(msg + "\n");
        });
    }

    private synchronized void updateNickNameColumn() {
        Platform.runLater(() -> {
            ObservableList observableListNick = FXCollections.observableArrayList(nickList);
            nickNameColumn.getItems().clear();
            nickNameColumn.setItems(observableListNick);
        });
    }

    //Вызов диалогов
    private void loadDialog(String currentDialog) {
        switch (currentDialog) {
            case "setNickName":
                //Загрузка формы setNickName
                try {
                    System.out.println("Загрузка формы setNickName");
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/fxml/SetNickName.fxml"));
                    Parent fxmlSettings = fxmlLoader.load();
                    setNickNameController = fxmlLoader.getController();
                    System.out.println("setNickName создание диалога");
                    Stage setNickNameStage = createDialog(fxmlSettings, "Укажите имя пользователя", 115, 215);
                    setNickNameController.setNickNameStage(setNickNameStage);
                    setNickNameStage.showAndWait(); //Ожидание закрытия окна
                    loadConfigure();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case "about":
                try {
                    System.out.println("Загрузка формы about");
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/fxml/About.fxml"));
                    Parent fxmlSettings = fxmlLoader.load();
                    aboutController = fxmlLoader.getController();
                    System.out.println("about создание диалога");
                    Stage aboutStage = createDialog(fxmlSettings, "О программе", 310, 936);
                    aboutController.setAboutStage(aboutStage);
                    aboutStage.showAndWait(); //Ожидание закрытия окна
                    loadConfigure();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //Обработка нажатий кнопок меню
    public void actionButtonPerssed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();

        //Если кнопка не нажата - выходит из метода
        if (!(source instanceof MenuItem)) {
            System.out.println("Пока");
            return;
        }

        MenuItem clickedButton = (MenuItem) source;

        System.out.println("Сработала кнопка " + clickedButton.getId());
        loadDialog(clickedButton.getId());
    }

    //Загрузка настроек
    //Если настроек ранее не было - создается фйайл настроек со значениями полей None
    private void loadConfigure() {
        FileConfig fileConfig = new FileConfig();
        File file = new File(fileConfig.FILECONFIG);
        if (file.exists()) {
            System.out.println("Файла настроек загружен");
            settingsXML = fileConfig.loadXML();
            printMsg("Настройки загружены");
            if (DEFAULT_USER_NAME.equals(this.userName) && (!settingsXML.getUser().equals("None"))) {
                connection.sendMsg("/MyNickName " + settingsXML.getUser());
                this.userName = settingsXML.getUser();
                Platform.runLater(() -> {
                    labelStatus.setText("Ваш ник: " + this.userName);
                });
            }
            if (!this.userName.equals(settingsXML.getUser())) {
                connection.sendMsg("/ReplaceMyNickName " + this.userName + " - " + settingsXML.getUser());
                this.userName = settingsXML.getUser();
                Platform.runLater(() -> {
                    labelStatus.setText("Ваш ник: " + this.userName);
                });
            }
            System.out.println(settingsXML.toString());
        } else {
            fileConfig.createXML("None", "None", "None", "None");
            System.out.println("Создан ранее не существоваший файл настроек");
            settingsXML = fileConfig.loadXML();
            connection.sendMsg("/MyNickName " + settingsXML.getUser());
            this.userName = settingsXML.getUser();
            Platform.runLater(() -> {
                labelStatus.setText("Ваш ник: " + this.userName);
            });
        }

        if (settingsXML.getUser().equals("None")) {
            System.out.println("Настроек на данный момент нет");
            printMsg("Настроек на данный момент нет");
            loadDialog("setNickName");
            System.out.println(settingsXML.toString());
        }

        if (settingsXML.getUserName().equals("None")) {
            printMsg("Нет параметров для авторизации");
            Platform.runLater(() -> {
                labelStatus.setText("Ваш ник: " + this.userName + "\t На данный момент у вас нет данных для авторизации");
            });
        } else {
            Platform.runLater(() -> {
                labelStatus.setText("Ваш ник: " + this.userName + "\t На сервере вы известный как " + settingsXML.getUserName());
            });
        }
    }

    //Создание диалогового модального окна
    private Stage createDialog(Parent currentParent, String title, int height, int width) {
        System.out.println("Отрисовка диалогового окна " + title);
        Stage currentStage = new Stage();
        currentStage.setTitle(title);
        currentStage.setMinHeight(height);
        currentStage.setMinWidth(width);
        currentStage.setResizable(false);
        currentStage.setScene(new Scene(currentParent));
        currentStage.initModality(Modality.APPLICATION_MODAL);
        currentStage.initOwner(mainStage);
        return currentStage;
    }

    public void registerUser() {
        if (!"None".equals(settingsXML.getUserName()) && !"None".equals(settingsXML.getPassword())) {
            this.connection.sendMsg("/Register " + settingsXML.getUserName() + " " + settingsXML.getPassword());
        } else {
            printMsg("Сначала укажите данные для авторизации");
        }
    }

    public void authUser() {
        printMsg("Попытка авторизации");
        if (!"None".equals(settingsXML.getUserName()) && !"None".equals(settingsXML.getPassword())) {
            this.connection.sendMsg("/AuthUser " + settingsXML.getUserName() + " " + settingsXML.getPassword());
        }
    }
}
