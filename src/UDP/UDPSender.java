package UDP;

import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSender {
    JTextArea textArea = new JTextArea();
    JTextField msg = new JTextField(40);
    JButton button = new JButton("Send Message");
    public UDPSender() throws Exception {
        JFrame frame = new SenderUI();
        int port = 9876;
        DatagramSocket serverSocket = new DatagramSocket(port);
        System.out.println("Server is running...");
        //byte[] receiveData = new byte[1024];
        //byte[] sendData  = new byte[1024];

        while(true) {
            byte[] receiveData = new byte[1024];
            byte[] sendData    = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket (receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            String sentence = new String(receivePacket.getData());
            sentence = sentence.trim();
            System.out.println("Message from client: "+sentence);
            textArea.append("Message from client: "+sentence +"\n");
            InetAddress IPAddress = receivePacket.getAddress();
            int clientPort = receivePacket.getPort();
            String capitalizedSentence = sentence.toUpperCase();
            sendData = capitalizedSentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket  (sendData, sendData.length, IPAddress, clientPort);
            serverSocket.send(sendPacket);

        }
    }
    public static void main(String args[]) throws Exception {
        UDPSender sender = new UDPSender();

    }

     class SenderUI extends JFrame {
        SenderUI(){
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(500,500);
            JScrollPane usersScrollPane = new JScrollPane(textArea);
            usersScrollPane.setBounds(392, 54, 163, 368);
            usersScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            setVisible(true);
            JPanel msgPanel = new JPanel();
            msgPanel.add(msg);
            add(usersScrollPane, BorderLayout.CENTER);

        }
    }
}
