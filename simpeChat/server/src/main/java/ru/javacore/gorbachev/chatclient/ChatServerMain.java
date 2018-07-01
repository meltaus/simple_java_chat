package ru.javacore.gorbachev.chatclient;

import ru.javacore.gorbachev.chatclient.server.ChatServer;

public class ChatServerMain {
    private static ChatServer chatServer;

    public static void main(String[] args) {
        chatServer = new ChatServer();
    }
}
