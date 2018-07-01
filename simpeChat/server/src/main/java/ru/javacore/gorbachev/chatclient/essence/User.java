package ru.javacore.gorbachev.chatclient.essence;

public class User {
    private final int id;
    private final String nickName;
    private final String password;

    public User(int id, String nickName, String password) {
        this.id = id;
        this.nickName = nickName;
        this.password = password;
    }

    public User(String nickName, String password) {
        this.id = 0;
        this.nickName = nickName;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
