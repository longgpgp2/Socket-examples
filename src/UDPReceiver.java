
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPReceiver {
    private JTextArea textArea;
    private JTextField textInput;
    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int port = 9876;

    public UDPReceiver() throws Exception {
        clientSocket = new DatagramSocket();
        serverAddress = InetAddress.getByName("localhost");

        textArea = new JTextArea();
        textInput = new JTextField(30);
        ReceiverUI ui = new ReceiverUI(textArea, textInput, this::sendMessage);
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
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
            clientSocket.send(sendPacket);

            // Start a new thread to receive the response
            new Thread(this::receiveMessage).start();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error sending message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void receiveMessage() {
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String response = new String(receivePacket.getData()).trim();
            SwingUtilities.invokeLater(() -> {
                textArea.append("FROM SERVER: " + response + "\n");
            });
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Error receiving message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    public static void main(String[] args) throws Exception {
        new UDPReceiver();
    }
}

class ReceiverUI extends JFrame {
    ReceiverUI(JTextArea textArea, JTextField textInput, Runnable sendMessageAction) {
        setTitle("UDP Receiver");
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