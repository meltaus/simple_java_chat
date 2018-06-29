package ru.javacore.gorbachev.chatclient.network.connection;

import ru.javacore.gorbachev.chatclient.network.listener.TCPConnectionListener;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    //Класс описывающий одно содениене

    private final Socket socket;
    //Поток, который слушает входящее соединение
    private final Thread rxThread;
    //Потоки ввода\вывода сообщений
    private final BufferedReader inMsg;
    private final BufferedWriter outMsg;
    //Слушатель событий
    private final TCPConnectionListener listenerEvent;

    //Создаем сокет
    public TCPConnection(TCPConnectionListener listenerEvent, String ip, int port) throws IOException {
        this(new Socket(ip, port), listenerEvent);
    }

    //Создаем соединение на основе полученного сокета
    public TCPConnection(Socket socket, TCPConnectionListener tcpConnectionListener) throws IOException {
        System.out.println("Сокет получен, соединение установлено");
        this.socket = socket;
        this.listenerEvent = tcpConnectionListener;
        this.inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        this.outMsg = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        this.rxThread = new Thread(() -> threadConnection());
        this.rxThread.start();
    }

    private void threadConnection() {
        System.out.println("Запущен поток обработки клиента");
        try{
            listenerEvent.onConnectionReady(TCPConnection.this);
            while (!rxThread.isInterrupted()) {
                listenerEvent.onReciveMsg(TCPConnection.this, inMsg.readLine());
            }
        } catch (IOException e) {
            listenerEvent.onExeption(TCPConnection.this, e);
        } finally {
            listenerEvent.onDisconect(TCPConnection.this);
        }
    }

    //Отправка сообщения
    public synchronized void sendMsg(String value) {
        try {
            this.outMsg.write(value + "\r\n");
            this.outMsg.flush();
        } catch (IOException e) {
            this.listenerEvent.onExeption(TCPConnection.this, e);
            disconnect();
        }
    }

    //Закрыть соединение
    public synchronized void disconnect() {
        this.rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            this.listenerEvent.onExeption(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "Соединение " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
