package UDP;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastReceiver {
    private JTextArea textArea;
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private int port = 9876;

    public MulticastReceiver() throws Exception {
        multicastSocket = new MulticastSocket(port);
        multicastAddress = InetAddress.getByName("224.0.0.1"); // Example multicast address
        multicastSocket.joinGroup(multicastAddress); // Join the multicast group

        textArea = new JTextArea();
        ReceiverUI ui = new ReceiverUI(textArea);
        ui.setVisible(true);

        // Start a new thread to listen for messages
        new Thread(this::receiveMessages).start();
    }

    private void receiveMessages() {
        try {
            byte[] receiveData = new byte[1024];
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                multicastSocket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0 , receivePacket.getLength()).trim();
                SwingUtilities.invokeLater(() -> {
                    textArea.append("FROM MULTICAST: " + message + "\n");
                });
            }
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Error receiving message: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    public static void main(String[] args) throws Exception {
        new MulticastReceiver();
    }
}

class ReceiverUI extends JFrame {
    ReceiverUI(JTextArea textArea) {
        setTitle("Multicast Receiver");
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