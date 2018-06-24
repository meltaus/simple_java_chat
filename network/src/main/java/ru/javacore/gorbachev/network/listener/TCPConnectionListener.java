package ru.javacore.gorbachev.network.listener;

import ru.javacore.gorbachev.network.connection.TCPConnection;

public interface TCPConnectionListener {

    void onConnectionReady(TCPConnection tcpConnection);
    void onReciveMsg(TCPConnection tcpConnection, String value);
    void onDisconect(TCPConnection tcpConnection);
    void onExeption(TCPConnection tcpConnection, Exception e);
}
