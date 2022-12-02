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

            new Thread(() -> {
                try {
                    client.listenMessage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            client.writeMessage();
        } catch (IOException e) {
            System.out.println("An error occurred starting client connection.");
            e.printStackTrace();
        }
    }

    @Override
    public void StartServer() {
        Server.start(port);
    }
}
