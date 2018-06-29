package ru.javacore.gorbachev.essence;

public class UserChat {

    private String userName;
    private String tcpConntecion;

    public UserChat(String userName, String tcpConntecion) {
        this.userName = userName;
        this.tcpConntecion = tcpConntecion;
    }

    public String getTcpConntecion() {
        return tcpConntecion;
    }

    public void setTcpConntecion(String tcpConntecion) {
        this.tcpConntecion = tcpConntecion;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Имя пользователя: " + this.userName;
    }
}
