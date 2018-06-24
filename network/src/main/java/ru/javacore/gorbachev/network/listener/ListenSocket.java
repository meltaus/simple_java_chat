package ru.javacore.gorbachev.network.listener;

import ru.javacore.gorbachev.network.connection.TCPConnection;
import ru.javacore.gorbachev.network.listener.interfaces.TCPConnectionListener;

import java.io.BufferedReader;
import java.io.IOException;

public class ListenSocket implements Runnable {

    private final BufferedReader inMsg;
    private final TCPConnectionListener listenerEvent;
    private final TCPConnection tcpConnection;

    public ListenSocket(BufferedReader in, TCPConnectionListener tcpConnectionListener, TCPConnection tcpConnection) {
        this.listenerEvent = tcpConnectionListener;
        this.inMsg = in;
        this.tcpConnection = tcpConnection;
    }

    public void run() {
        try {
            while (true) {
                this.listenerEvent.onReciveMsg(this.tcpConnection, this.inMsg.readLine());
            }
        } catch (IOException e) {
            //Ошибка чтения из потока
            e.printStackTrace();
        } finally {

        }
    }
}
