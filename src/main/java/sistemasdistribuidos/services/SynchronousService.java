package sistemasdistribuidos.services;

import sistemasdistribuidos.interfaces.ISynchronousService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SynchronousService implements ISynchronousService {
    private static int primaryPort;
    private static int secondaryPort;
    private static String host;

    public SynchronousService(){
        primaryPort = 8081;
        secondaryPort = 8082;
        host = "localhost";
    }

    @Override
    public void ClientMode() {
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

    @Override
    public void ServerMode() {
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

    @Override
    public void Execute(String[] args) {
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

    private static ServerSocket ReceiveFrom(int port) throws IOException {
        return new ServerSocket(port);
    }

    private static Socket ConnectTo(int port) throws IOException {
        return new Socket(host, port);
    }
}
