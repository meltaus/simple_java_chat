package ru.javacore.gorbachev.network.connection;

import ru.javacore.gorbachev.network.listener.ListenSocket;
import ru.javacore.gorbachev.network.listener.interfaces.TCPConnectionListener;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    //Класс описывающий одно содениене

    private final Socket socket;
    //Поток, который слушает входящее соединение
    private final Runnable listenSocket;
    private final Thread rxThread;
    //Потоки ввода\вывода сообщений
    private final BufferedReader inMsg;
    private final BufferedWriter outMsg;
    //Слушатель событий
    private final TCPConnectionListener listenerEvent;

    //Создаем соединение на основе полученного сокета
    public TCPConnection(Socket socket, TCPConnectionListener tcpConnectionListener) throws IOException {
        this.listenerEvent = tcpConnectionListener;
        this.socket = socket;
        this.inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        this.outMsg = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        this.listenSocket = new ListenSocket(this.inMsg, this.listenerEvent, TCPConnection.this);
        this.rxThread = new Thread(this.listenSocket);
        this.rxThread.start();
    }
}
