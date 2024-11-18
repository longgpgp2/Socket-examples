package TCP;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPSender {
    private JTextArea textArea;
    private JTextField textInput;
    private Socket socket;
    private PrintWriter out;
    private String serverAddress = "localhost"; // Change this to the server's IP if needed
    private int port = 9876;

    public TCPSender() {
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error connecting to server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        textArea = new JTextArea();
        textInput = new JTextField(30);
        SenderUI ui = new SenderUI(textArea, textInput, this::sendMessage);
        ui.setVisible(true);
    }

    public void sendMessage() {
        String message = textInput.getText();
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Message cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        out.println(message);
        textArea.append("Sent: " + message + "\n");
        textInput.setText("");
    }

    public static void main(String[] args) {
        new TCPSender();
    }
}

class SenderUI extends JFrame {
    SenderUI(JTextArea textArea, JTextField textInput, Runnable sendMessageAction) {
        setTitle("TCP Sender");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // Center the window

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        JButton sendButton = new JButton("Send Message");
        sendButton.addActionListener(e -> sendMessageAction.run());

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(textInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
    }
}