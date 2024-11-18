package chat;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Map<String, PrintWriter> clientWriters = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // Get username from client
                out.println("Enter your username:");
                username = in.readLine();
                synchronized (clientWriters) {
                    clientWriters.put(username, out);
                    broadcastUserList();
                }

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Received from " + username + ": " + message);
                    // Parse message to get recipient
                    String[] parts = message.split(":", 2);
                    if (parts.length == 2) {
                        String recipient = parts[0].trim();
                        String msg = parts[1].trim();
                        sendMessageToClient(recipient, msg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientWriters) {
                    clientWriters.remove(username);
                    broadcastUserList();
                }
            }
        }

        private void sendMessageToClient(String recipient, String message) {
            PrintWriter writer = clientWriters.get(recipient);
            if (writer != null) {
                writer.println(username + ": " + message);
            } else {
                out.println("User  " + recipient + " is not connected.");
            }
        }

        private void broadcastUserList() {
            StringBuilder userList = new StringBuilder("Connected users: ");
            for (String user : clientWriters.keySet()) {
                userList.append(user).append(", ");
            }
            // Remove the last comma and space
            if (userList.length() > 0) {
                userList.setLength(userList.length() - 2);
            }
            // Notify all clients of the updated user list
            for (PrintWriter writer : clientWriters.values()) {
                writer.println(userList.toString());
            }
        }
    }
}