package ru.javacore.gorbachev.network.Listener;

import java.io.BufferedReader;
import java.io.IOException;

public class ListenSocket implements Runnable {

    private final BufferedReader inMsg;

    public ListenSocket(BufferedReader in) {
        this.inMsg = in;
    }

    public void run() {
        try {
            String msg = this.inMsg.readLine();
        } catch (IOException e) {
            //Ошибка чтения из потока
            e.printStackTrace();
        } finally {
            
        }
    }
}
