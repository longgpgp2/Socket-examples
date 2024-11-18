package UDP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class UDPReceiver extends JPanel {
    JFrame frame;
    JTextArea textArea = new JTextArea();;
    JPanel msgPane;
    JButton button;
    JTextField textInput = new JTextField(30);

    public UDPReceiver() throws Exception {

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        int port = 9876;
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
        button = new JButton("Send Message");
        System.out.println("enter");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sendMessage(clientSocket, port, IPAddress);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        frame = new ReceiverUI();
//        frame.getContentPane().setLayout(null);
        frame.setVisible(true);


        //byte[] sendData = new byte[1024];
        //byte[] receiveData = new byte[1024];


    }

    public void sendMessage(DatagramSocket clientSocket, int port, InetAddress IPAddress) throws IOException {
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];

            String sentence = textInput.getText();
            textArea.append("Client Input: " + textInput.getText() +"\n");
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket (sendData,  sendData.length, IPAddress, port);
            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket  (receiveData, receiveData.length);
            clientSocket.receive(receivePacket);
            String modifiedSentence = new String(receivePacket.getData());
            modifiedSentence = modifiedSentence.trim();
            System.out.println("FROM SERVER:" + modifiedSentence);
            textArea.append("FROM SERVER:" + modifiedSentence+"\n");

            //clientSocket.close();


    }

    public static void main(String[] args) throws Exception {
        UDPReceiver receiver = new UDPReceiver();

    }

    class ReceiverUI extends JFrame{
        ReceiverUI(){
            this.setDefaultCloseOperation(EXIT_ON_CLOSE);
            this.setBounds(100, 100, 584, 578);

            msgPane = new JPanel();
            textInput.setSize(new Dimension(300,50));
            msgPane.setSize(new Dimension(300,50));
            msgPane.add(textInput);

//            this.setFocusable(true);
            textArea.setEditable(false);
            textArea.append("Enter your message... " +"\n");
            JScrollPane usersScrollPane = new JScrollPane(textArea);
            usersScrollPane.setBounds(392, 54, 163, 368);
            usersScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            this.add(msgPane, BorderLayout.SOUTH);
            this.add(button, BorderLayout.EAST);
            this.add(usersScrollPane, BorderLayout.CENTER);
        }
    }

}
