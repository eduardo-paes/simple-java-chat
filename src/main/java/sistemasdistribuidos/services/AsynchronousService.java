package sistemasdistribuidos.services;

import sistemasdistribuidos.interfaces.IAsynchronousService;
import sistemasdistribuidos.interfaces.IMessageService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class AsynchronousService implements IAsynchronousService {
    private static int port;
    private static String host;
    private static IMessageService messageService;

    public AsynchronousService(IMessageService messenger) {
        port = 8080;
        host = "localhost";
        messageService = messenger;
    }

    @Override
    public void StartClientCommunication() {

    }

    @Override
    public void StartServer() {

    }

    private void OpenServerConnection() {

        try {
            // Open server socket
            ServerSocket ss = new ServerSocket(port);
            Socket socket = ss.accept();

            // Common declarations
            String str;
            DataInputStream receiver;
            DataOutputStream sender;
            Scanner in = new Scanner(System.in);

            // Read message from client
            receiver = new DataInputStream(socket.getInputStream());
            str = receiver.readUTF();
            System.out.println("Client: " + str);
            ss.close();

        } catch (IOException e){
            System.out.println("An error occurred opening server connection.");
            e.printStackTrace();
        }
    }
}
