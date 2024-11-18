package UDP;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender {
    private JTextArea textArea;
    private JTextField textInput;
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private int port = 9876;

    public MulticastSender() throws Exception {
        multicastSocket = new MulticastSocket();
        multicastAddress = InetAddress.getByName("224.0.0.1"); // Example multicast address

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

        try {
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, multicastAddress, port);
            new Thread(() -> {
                try {
                    multicastSocket.send(sendPacket);
                    textArea.append("Sent: " + message + "\n");
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Error sending message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error preparing message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) throws Exception {
        new MulticastSender();
    }
}

class SenderUI extends JFrame {
    SenderUI(JTextArea textArea, JTextField textInput, Runnable sendMessageAction) {
        setTitle("Multicast Sender");
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