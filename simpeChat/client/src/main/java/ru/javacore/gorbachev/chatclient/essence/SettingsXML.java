package ru.javacore.gorbachev.chatclient.essence;

public class SettingsXML {
    private String serverChat;
    private String user;
    private String userName;
    private String password;

    public String getServerChat() {
        return serverChat;
    }

    public void setServerChat(String serverChat) {
        this.serverChat = serverChat;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Settings: serverChat = " + this.serverChat + " User = " + this.user +
                " UserName = " + this.userName + " password = " + this.password;
    }
}
