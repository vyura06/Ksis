package client;

import connection.Connection;
import connection.Message;
import connection.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {
    private Connection connection;
    private final ModelGuiClient model;
    private final ViewGuiClient gui;
    volatile boolean isConnect, running;

    public Client() {
        model = new ModelGuiClient();
        gui = new ViewGuiClient(this);
        gui.initFrameClient();
        start();
    }

    public void run() {
        running = true;
        while (running) {
            while (!isConnect)
                Thread.onSpinWait();
            nameUserRegistration();
            receiveMessageFromServer();
        }
    }

    void connectToServer() {
        if (!isConnect) {
            try {
                connection = new Connection(new Socket(gui.getAddress(), gui.getPort()));
                isConnect = true;
                gui.addMessage("Сервисное сообщение: Вы подключились к серверу.\n");
            } catch (Exception e) {
                gui.errorDialogWindow("Произошла ошибка! Возможно Вы ввели не верный адрес сервера или порт. Попробуйте еще раз");
            }
        } else {
            gui.errorDialogWindow("Вы уже подключены!");
        }
    }

    void nameUserRegistration() {
        try {
            label:
            while (running) {
                String nameUser;
                Message msg = connection.receive();
                switch (msg.getTypeMessage()) {
                    case REQUEST_NAME_USER:
                        nameUser = gui.getNameUser();
                        connection.send(new Message(MessageType.USER_NAME, nameUser));
                        break;
                    case NAME_USED:
                        gui.errorDialogWindow("Данное имя уже используется, введите другое");
                        nameUser = gui.getNameUser();
                        connection.send(new Message(MessageType.USER_NAME, nameUser));
                        break;
                    case NAME_ACCEPTED:
                        gui.addMessage("Сервисное сообщение: ваше имя принято!\n");
                        model.setUsers(msg.getListUsers());
                        break label;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            gui.errorDialogWindow("Произошла ошибка при регистрации имени. Попробуйте переподключиться");
            try {
                connection.close();
                isConnect = false;
            } catch (IOException ex) {
                gui.errorDialogWindow("Ошибка при закрытии соединения");
            }
        }
    }

    void sendMessageOnServer(String text) {
        try {
            connection.send(new Message(MessageType.TEXT_MESSAGE, text));
        } catch (Exception e) {
            gui.errorDialogWindow("Ошибка при отправки сообщения");
        }
    }

    void receiveMessageFromServer() {
        while (isConnect) {
            try {
                Message msg = connection.receive();
                switch (msg.getTypeMessage()) {
                    case TEXT_MESSAGE:
                        gui.addMessage(msg.getTextMessage());
                        break;
                    case USER_ADDED:
                        model.addUser(msg.getTextMessage());
                        gui.refreshListUsers(model.getUsers());
                        gui.addMessage(String.format("Сервисное сообщение: пользователь %s присоединился к чату.\n",
                                msg.getTextMessage()));
                        break;
                    case REMOVED_USER:
                        model.removeUser(msg.getTextMessage());
                        gui.refreshListUsers(model.getUsers());
                        gui.addMessage(String.format("Сервисное сообщение: пользователь %s покинул чат.\n",
                                msg.getTextMessage()));
                        break;
                }
            } catch (Exception e) {
                gui.errorDialogWindow("Ошибка при приеме сообщения от сервера.");
                gui.refreshListUsers(model.getUsers());
                isConnect = false;
            }
        }
    }

    void disableClient() {
        try {
            if (isConnect) {
                connection.send(new Message(MessageType.DISABLE_USER));
                model.getUsers().clear();
                gui.refreshListUsers(model.getUsers());
                isConnect = false;
            } else {
                gui.errorDialogWindow("Вы уже отключены.");
            }
        } catch (Exception e) {
            gui.errorDialogWindow("Сервисное сообщение: произошла ошибка при отключении.");
        }
        interrupt();
    }

    @Override
    public void interrupt() {
        running = false;
        super.interrupt();
    }

    public static void main(String[] args) {
        new Client();
    }
}
