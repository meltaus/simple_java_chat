package ru.javacore.gorbachev.network.connection;

import ru.javacore.gorbachev.network.listener.TCPConnectionListener;

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

    //Создаем соединение на основе полученного сокета
    public TCPConnection(Socket socket, TCPConnectionListener tcpConnectionListener) throws IOException {
        this.listenerEvent = tcpConnectionListener;
        this.socket = socket;
        this.inMsg = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        this.outMsg = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
        this.rxThread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (!rxThread.isInterrupted()) {
                        listenerEvent.onReciveMsg(TCPConnection.this, inMsg.readLine());
                    }
                } catch (IOException e) {
                    //Ошибка чтения из потока
                    listenerEvent.onExeption(TCPConnection.this, e);
                } finally {
                    listenerEvent.onDisconect(TCPConnection.this);
                }
            }
        });
        this.rxThread.start();
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
        return "TCPConntection " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
