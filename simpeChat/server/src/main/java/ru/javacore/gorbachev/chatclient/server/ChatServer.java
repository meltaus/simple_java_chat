package ru.javacore.gorbachev.chatclient.server;

import ru.javacore.gorbachev.chatclient.network.connection.TCPConnection;
import ru.javacore.gorbachev.chatclient.essence.UserChat;
import ru.javacore.gorbachev.chatclient.network.listener.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener {
    private int port = 8189;
    private TCPConnection tcpConnection;
    //Список соединений
    private final ArrayList<TCPConnection> connections;
    //Список всех пользователей чата
    private final ArrayList<UserChat> userChat = new ArrayList<>();

    public ChatServer() {
        this.connections = new ArrayList<>();
        System.out.println("Сервер запущен");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    this.tcpConnection = new TCPConnection(serverSocket.accept(), this);
                } catch (IOException e) {
                    onExeption(this.tcpConnection, e);
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

    //Отправка личного сообщения
    private void sendCurrnetUser(String value, TCPConnection tcpConnection, UserChat userChat) {
        tcpConnection.sendMsg(userChat.getUserName() + " пишет вам:" + value);
    }
    private void sendCurrnetUser(String value, TCPConnection tcpConnection) {
        tcpConnection.sendMsg("Я пишу:" + value);
    }

    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        this.connections.add(tcpConnection);
    }

    public synchronized void onReciveMsg(TCPConnection tcpConnection, String value) {
        System.out.println(value);
        if (value.contains("/MyNickName")){
            value = value.replace("/MyNickName ", "");
            setUserChat(value, tcpConnection);
        } else if (value.contains("/ReplaceMyNickName")) {
            value = value.replace("/ReplaceMyNickName", "");
            String[] nick = value.split(" - ");
            nick[0] = nick[0].replace(" ", "");
            nick[1] = nick[1].replace(" ", "");
            replraseUserChat(nick, tcpConnection);
        } else if (value.contains("/AllNicks")) {
            sendAllUserChat();
        } else if (value.contains("/w")) {
            String workString = value.replace("/w ", "");
            String[] nick = workString.split(" ");
            value = value.replace("/w " + nick[1],"");
            value = value.replace(nick[0], "");
            int index = searchUserChat(nick[1], tcpConnection);
            if (index > -1) {
                sendCurrnetUser(value, this.connections.get(index), this.userChat.get(searchUserChat(tcpConnection)));
            }
            nick[0] = nick[0].replace(":", "");
            index = searchUserChat(nick[0], tcpConnection);
            if (index > -1) {
                sendCurrnetUser(value, this.connections.get(index));
            }
        } else {
            sendAll(value);
        }

    }

    public synchronized void onDisconect(TCPConnection tcpConnection) {
        this.connections.remove(tcpConnection);
        sendAll("Отключился клиент " + this.userChat.get(searchUserChat(tcpConnection)).getUserName());
        removeUserChat(tcpConnection);
    }

    public synchronized void onExeption(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection ошибка " + tcpConnection + ": " + e);
    }

    private void setUserChat(String userName, TCPConnection tcpConnection) {
        this.userChat.add(new UserChat(userName, tcpConnection.toString()));
        sendAll("Подключился клиент " + userName);
        sendAllUserChat();
    }

    private void sendAllUserChat() {
        int count = this.userChat.size();
        String allNicks = "";
        for (int i = 0; i < count; i++) {
            if (i == count-1) {
                allNicks += this.userChat.get(i).getUserName();
            } else {
                allNicks += this.userChat.get(i).getUserName() + " - ";
            }
        }
        System.out.println(allNicks);
        sendAll("/AllNicks " + allNicks);
    }

    private void replraseUserChat(String[] nick, TCPConnection tcpConnection) {
        int index = searchUserChat(tcpConnection);
        if (index > -1) {
            sendAll("Пользователь " + this.userChat.get(index).getUserName() + " " +
                    "теперь известен как " + nick[1]);
            this.userChat.remove(index);
            setUserChat(nick[1], tcpConnection);
        }
        for (int i = 0; i < this.userChat.size(); i++) {
            System.out.println(this.userChat.get(i));
        }
    }

    private void removeUserChat(TCPConnection tcpConnection) {
        int index = searchUserChat(tcpConnection);
        if (index > -1) {
            this.userChat.remove(index);
            sendAllUserChat();
        }
    }

    private int searchUserChat(TCPConnection tcpConnection) {
        int count = this.userChat.size();
        for (int i = 0; i < count; i++) {
            if (tcpConnection.toString().equals(this.userChat.get(i).getTcpConntecion())){
                return i;
            }
        }

        return -1;
    }

    private int searchUserChat(String nickName, TCPConnection tcpConnection) {
        int count = this.userChat.size();
        for (int i = 0; i < count; i++) {
            if (this.userChat.get(i).getUserName().equals(nickName)){
                return i;
            }
        }

        return -1;
    }

    public ArrayList<UserChat> getUserChat() {
        return userChat;
    }
}
