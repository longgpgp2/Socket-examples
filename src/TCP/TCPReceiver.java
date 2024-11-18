package TCP;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPReceiver {
    private JTextArea textArea;
    private ServerSocket serverSocket;
    private int port = 9876;

    public TCPReceiver() {
        try {
            serverSocket = new ServerSocket(port);
            textArea = new JTextArea();
            ReceiverUI ui = new ReceiverUI(textArea);
            ui.setVisible(true);

            new Thread(this::listenForConnections).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error starting server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void listenForConnections() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error accepting connection: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                // Directly append to the text area from this thread
                textArea.append("FROM CLIENT: " + message + "\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error reading message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error closing client socket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new TCPReceiver();
    }
}

class ReceiverUI extends JFrame {
    ReceiverUI(JTextArea textArea) {
        setTitle("TCP Receiver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null); // Center the window

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        add(scrollPane, BorderLayout.CENTER);
    }
}