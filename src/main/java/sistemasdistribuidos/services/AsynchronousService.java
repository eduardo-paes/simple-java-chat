package sistemasdistribuidos.services;

import sistemasdistribuidos.interfaces.IAsynchronousService;
import sistemasdistribuidos.models.Client;
import sistemasdistribuidos.models.Server;

import java.io.IOException;

public class AsynchronousService implements IAsynchronousService {
    private static int port;
    private static String host;

    public AsynchronousService() {
        port = 8080;
        host = "localhost";
    }

    @Override
    public void StartClient() {
        try {
            Client client = new Client(host, port);
            client.connect();
        } catch (IOException e) {
            System.out.println("An error occurred starting client connection.");
            e.printStackTrace();
        }
    }

    @Override
    public void StartServer() {
        Server.start(port);
    }

    @Override
    public void Execute(String[] args) {
        if (args.length > 0) {
            // Server
            if (Integer.parseInt(args[0]) == 0) {
                StartServer();
            }
            // Client
            else {
                StartClient();
            }
        } else {
            System.out.println("No parameters informed.");
        }
    }
}
