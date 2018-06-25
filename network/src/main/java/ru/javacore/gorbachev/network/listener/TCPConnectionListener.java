package ru.javacore.gorbachev.network.listener;

import ru.javacore.gorbachev.network.connection.TCPConnection;

public interface TCPConnectionListener {

    //Подключение
    void onConnectionReady(TCPConnection tcpConnection);
    //Отправка сообщения
    void onReciveMsg(TCPConnection tcpConnection, String value);
    //Отключение
    void onDisconect(TCPConnection tcpConnection);
    //Ошибка
    void onExeption(TCPConnection tcpConnection, Exception e);
}