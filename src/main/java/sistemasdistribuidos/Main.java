package sistemasdistribuidos;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {
    protected static int port = 8080;
    protected static String host = "localhost";

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
                socket = ConnectTo();

                // Send message to server
                str = in.nextLine();
                sender = new DataOutputStream(socket.getOutputStream());
                sender.writeUTF(str);
                sender.flush();
                sender.close();
                socket.close();

                // Stop if client write 'stop'
                if (str.trim().toLowerCase().equals("stop")) break;

                // Receive from server
                ss = ReceiveFrom();
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
                ss = ReceiveFrom();
                socket = ss.accept();

                // Read message from client
                receiver = new DataInputStream(socket.getInputStream());
                str = receiver.readUTF();
                System.out.println("Client: " + str);
                ss.close();

                // Stop if server write 'stop'
                if (str.trim().toLowerCase().equals("stop")) break;

                // Connect to client
                socket = ConnectTo();

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

    private static ServerSocket ReceiveFrom() {
        while (true) {
            try {
                return new ServerSocket(port);
            } catch (Exception e){
                // Wait to connect
            }
        }
    }

    private static Socket ConnectTo() {
        while (true) {
            try {
                return new Socket(host, port);
            } catch (Exception e){
                // Wait to connect
            }
        }
    }
}
