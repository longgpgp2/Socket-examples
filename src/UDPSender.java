
import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPSender {
    private JTextArea textArea = new JTextArea();

    public UDPSender() throws Exception {
        int port = 9876;
        DatagramSocket serverSocket = new DatagramSocket(port);
        System.out.println("Server is running...");

        SenderUI ui = new SenderUI(textArea);

        while (true) {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData()).trim();
            System.out.println("Message from client: " + sentence);
            textArea.append("Message from client: " + sentence + "\n");

            InetAddress IPAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            String capitalizedSentence = sentence.toUpperCase();
            byte[] sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, clientPort);
            serverSocket.send(sendPacket);
        }
    }

    public static void main(String[] args) throws Exception {
        new UDPSender();
    }
}

class SenderUI extends JFrame {
    SenderUI(JTextArea textArea) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        JScrollPane usersScrollPane = new JScrollPane(textArea);
        usersScrollPane.setBounds(392, 54, 163, 368);
        usersScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        add(usersScrollPane, BorderLayout.CENTER);
        setVisible(true);
    }
}