package server;

import connection.Connection;

import java.util.HashMap;
import java.util.Map;

public class ModelGuiServer {
    private final Map<String, Connection> allUsersMultiChat = new HashMap<>();

    public void addUser(String nameUser, Connection connection) {
        allUsersMultiChat.put(nameUser, connection);
    }
    public void removeUser(String nameUser) {
        allUsersMultiChat.remove(nameUser);
    }

    public Map<String, Connection> getAllUsersMultiChat() {
        return allUsersMultiChat;
    }
}
