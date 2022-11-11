package sistemasdistribuidos;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {
    private final static int primaryPort = 8081;
    private final static int secondaryPort = 8082;
    private final static String host = "localhost";

    public static void main(String[] args) {
        if (args.length > 0) {
            if (Integer.parseInt(args[0]) == 1) {
                ClientMode();
            }
            else {
                ServerMode();
            }
        }
        else {
            System.out.println("No parameters informed.");
        }
    }

    public static void ClientMode() {
        try {
            ServerSocket ss;
            Socket socket;

            // Common declarations
            String str;
            DataInputStream receiver;
            DataOutputStream sender;
            Scanner in = new Scanner(System.in);

            // Read and send message
            do {
                // Connect to Server
                socket = ConnectTo(primaryPort);

                // Send message to server
                str = in.nextLine();
                sender = new DataOutputStream(socket.getOutputStream());
                sender.writeUTF(str);
                sender.flush();
                sender.close();
                socket.close();

                // Stop if client write 'stop'
                if (str.trim().equalsIgnoreCase("stop")) break;

                // Receive from server
                ss = ReceiveFrom(secondaryPort);
                socket = ss.accept();

                // Read message from server
                receiver = new DataInputStream(socket.getInputStream());
                str = receiver.readUTF();
                System.out.println("Server: " + str);
                ss.close();
            } while (!str.equals("stop"));

        } catch (Exception e){
            System.out.println(e);
        }
    }

    public static void ServerMode() {
        try {
            // Open server socket
            ServerSocket ss;
            Socket socket;

            // Common declarations
            String str;
            DataInputStream receiver;
            DataOutputStream sender;
            Scanner in = new Scanner(System.in);

            // Read and send message
            do {
                // Receive from client
                ss = ReceiveFrom(primaryPort);
                socket = ss.accept();

                // Read message from client
                receiver = new DataInputStream(socket.getInputStream());
                str = receiver.readUTF();
                System.out.println("Client: " + str);
                ss.close();

                // Stop if server write 'stop'
                if (str.trim().equalsIgnoreCase("stop")) break;

                // Connect to client
                socket = ConnectTo(secondaryPort);

                // Send message to client
                str = in.nextLine();
                sender = new DataOutputStream(socket.getOutputStream());
                sender.writeUTF(str);
                sender.flush();
                sender.close();
                socket.close();
            } while (!str.equals("stop"));
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private static ServerSocket ReceiveFrom(int port) throws IOException {
        return new ServerSocket(port);
    }

    private static Socket ConnectTo(int port) throws IOException {
        return new Socket(host, port);
    }
}
