package chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 12345;
    private static String username;
    private static Socket socket;
    private static PrintWriter out;
    private static DefaultListModel<String> userListModel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        // Create and set up the UI components
        ChatClientUI chatClientUI = new ChatClientUI();
        frame.add(chatClientUI);

        frame.setVisible(true);

        try {
            socket = new Socket(SERVER_ADDRESS, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Get username from user
            username = JOptionPane.showInputDialog("Enter your username:");
            out.println(username);

            // Start a thread to receive messages
            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        chatClientUI.appendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static class ChatClientUI extends JPanel {
        private JList<String> userList;
        private JTextArea textArea;
        private JTextField messageField;

        public ChatClientUI() {
            setLayout(new BorderLayout());
            userListModel = new DefaultListModel<>();
            userList = new JList<>(userListModel);
            textArea = new JTextArea();
            messageField = new JTextField();

            JButton sendButton = new JButton("Send");
            sendButton.addActionListener(e -> sendMessage());

            messageField.addActionListener(e -> sendMessage());

            add(new JScrollPane(userList), BorderLayout.NORTH);
            add(new JScrollPane(textArea), BorderLayout.CENTER);
            add(messageField, BorderLayout.SOUTH);
            add(sendButton, BorderLayout.EAST);
        }

        public void appendMessage(String message) {
            textArea.append(message + "\n");
            if (message.startsWith("Connected users: ")) {
                updateUserList(message.substring("Connected users: ".length()).split(", "));
            }
        }

        private void updateUserList(String[] users) {
            SwingUtilities.invokeLater(() -> {
                userListModel.clear();
                for (String user : users) {
                    userListModel.addElement(user.trim());
                }
            });
        }

        private void sendMessage() {
            String message = messageField.getText();
            String recipient = userList.getSelectedValue();
            if (recipient != null && !message.isEmpty()) {
                out.println(recipient + ": " + message);
                messageField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a user and enter a message.");
            }
        }
    }
}
