package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class ViewGuiClient {
    private final Client client;
    private final JFrame frame = new JFrame("Чат");
    private final JTextArea messagesArea = new JTextArea(30, 20);
    private final JTextArea usersArea = new JTextArea(30, 15);
    private final JPanel panel = new JPanel();
    private final JTextField textField = new JTextField(40);
    private final JButton buttonDisable = new JButton("Отключиться");
    private final JButton buttonConnect = new JButton("Подключиться");

    public ViewGuiClient(Client client) {
        this.client = client;
    }

    protected void initFrameClient() {
        messagesArea.setEditable(false);
        usersArea.setEditable(false);

        buttonDisable.addActionListener(e -> client.disableClient());
        buttonConnect.addActionListener(e -> client.connectToServer());
        panel.add(buttonConnect);
        panel.add(buttonDisable);

        textField.addActionListener(e -> {
            client.sendMessageOnServer(textField.getText());
            textField.setText("");
        });
        panel.add(textField);

        frame.add(panel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(messagesArea), BorderLayout.CENTER);
        frame.add(new JScrollPane(usersArea), BorderLayout.EAST);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.isConnect)
                    client.disableClient();
            }
        });
        frame.setVisible(true);
    }

    protected void addMessage(String text) {
        messagesArea.append(text);
    }

    protected void refreshListUsers(Set<String> users) {
        usersArea.setText("");
        if (client.isConnect) {
            StringBuilder text = new StringBuilder("Список пользователей:\n");
            for (String user : users)
                text.append(user).append('\n');
            usersArea.append(text.toString());
        }
    }

    protected String getAddress() {
        return JOptionPane.showInputDialog(
                frame, "Введите адрес сервера:",
                "Ввод адреса сервера",
                JOptionPane.QUESTION_MESSAGE
        );
    }
    protected int getPort() {
        while (true) {
            String port = JOptionPane.showInputDialog(
                    frame, "Введите порт сервера:",
                    "Ввод порта сервера",
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                return Integer.parseInt(port);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        frame, "Введен неккоректный порт сервера. Попробуйте еще раз.",
                        "Ошибка ввода порта сервера", JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    protected String getNameUser() {
        return JOptionPane.showInputDialog(
                frame, "Введите имя пользователя:",
                "Ввод имени пользователя",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    protected void errorDialogWindow(String text) {
        JOptionPane.showMessageDialog(
                frame, text,
                "Ошибка", JOptionPane.ERROR_MESSAGE
        );
    }
}