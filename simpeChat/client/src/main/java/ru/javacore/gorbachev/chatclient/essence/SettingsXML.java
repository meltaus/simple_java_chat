package ru.javacore.gorbachev.chatclient.essence;

public class SettingsXML {
    private String serverChat;
    private String user;

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

    @Override
    public String toString() {
        return "Settings: serverChat = " + this.serverChat + " User = " + this.user;
    }
}
