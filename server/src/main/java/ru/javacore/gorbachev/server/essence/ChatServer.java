package ru.javacore.gorbachev.server.essence;

import ru.javacore.gorbachev.network.connection.TCPConnection;
import ru.javacore.gorbachev.network.listener.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {
    private int port = 8189;
    private TCPConnection tcpConnection;
    //Список соединений
    private final ArrayList<TCPConnection> connections;

    public ChatServer() {
        this.connections = new ArrayList<>();
        System.out.println("Сервер запущен");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    this.tcpConnection = new TCPConnection(serverSocket.accept(), this);
                } catch (IOException e) {
                    System.out.println("Ошибка соединения " + e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendAll(String values) {
        System.out.println(values);
        int count = connections.size();
        for (int i = 0; i < count; i++) {
            this.connections.get(i).sendMsg(values);
        }
    }

    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        this.connections.add(tcpConnection);
        sendAll("Подключился клиент " + tcpConnection);
    }

    public synchronized void onReciveMsg(TCPConnection tcpConnection, String value) {
        sendAll(value);
    }

    public synchronized void onDisconect(TCPConnection tcpConnection) {
        this.connections.remove(tcpConnection);
        sendAll("Отключился клиент " + tcpConnection);
    }

    public synchronized void onExeption(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection ошибка " + tcpConnection + ": " + e);
    }
}
