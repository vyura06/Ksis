package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ViewGuiServer {
    private final Server server;
    private final JFrame frame = new JFrame("Запуск сервера");
    private final JTextArea dialogWindow = new JTextArea(10, 40);
    private final JButton buttonStartServer = new JButton("Запустить сервер");
    private final JButton buttonStopServer = new JButton("Остановить сервер");
    private final JPanel panelButtons = new JPanel();

    public ViewGuiServer(Server server) {
        this.server = server;
    }

    protected void initFrameServer() {
        dialogWindow.setEditable(false);
        dialogWindow.setLineWrap(true);

        buttonStartServer.addActionListener(e -> server.startServer(getPort()));
        buttonStopServer.addActionListener(e -> server.stopServer());
        panelButtons.add(buttonStartServer);
        panelButtons.add(buttonStopServer);

        frame.add(new JScrollPane(dialogWindow), BorderLayout.CENTER);
        frame.add(panelButtons, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                server.stopServer();
            }
        });
        frame.setVisible(true);
    }

    public void refreshDialogWindowServer(String serviceMessage) {
        dialogWindow.append(serviceMessage);
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
}