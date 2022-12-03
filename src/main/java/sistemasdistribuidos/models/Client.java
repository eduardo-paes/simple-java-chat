package sistemasdistribuidos.models;

import sistemasdistribuidos.factories.AsynchronousFactory;
import sistemasdistribuidos.interfaces.IMessageService;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Client {
    //region Attributes
    private IMessageService messageService;
    private Socket socket;
    private OutputStream output;
    private Writer writer;
    private BufferedWriter buffer;
    private String name;
    private final String host;
    private final int port;
    private boolean listen;
    //endregion

    //region Constructor
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        setListen(true);
    }
    //endregion

    //region Methods
    public void connect() throws IOException {
        // Connect to server
        socket = new Socket(this.host,this.port);

        // Get input/output buffer/writer
        output = socket.getOutputStream();
        writer = new OutputStreamWriter(output);
        buffer = new BufferedWriter(writer);

        // Get name from client
        System.out.print("Introduce your name: ");
        Scanner in = new Scanner(System.in);
        name = in.nextLine();

        String messageFile = name.trim().toLowerCase() + "_messages.txt";
        messageService = AsynchronousFactory.getMessageService(messageFile);

        // Start chat loading old messages
        startChatMessages();

        // Start to read messages from server
        listenMessage();

        // Start to get messages from client to send to server
        writeMessage();
    }

    private void startChatMessages() {
        System.out.println("Chat started!\nLoading old messages...");
        this.messageService.LoadMessages();
        System.out.println("---");
    }

    private void listenMessage() {
        new Thread(() -> {
            try {
                InputStream in = socket.getInputStream();
                InputStreamReader inr = new InputStreamReader(in);
                BufferedReader bfr = new BufferedReader(inr);

                String msg;
                while(getListen()) {
                    if (bfr.ready()) {
                        msg = bfr.readLine();
                        messageService.SaveMessage(msg);
                        if (msg.toLowerCase().contains("disconnected")) {
                            msg = "Client disconnected.";
                        }
                        System.out.println(msg);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void writeMessage() throws IOException {
        Scanner in = new Scanner(System.in);
        String msg;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        while (getListen()) {
            msg = in.nextLine();

            if ("Exit".equalsIgnoreCase(msg)) {
                msg = "Disconnecting...";
                setListen(false);
            }

            msg = String.format("%s: %s - [%s]", name, msg, dtf.format(LocalDateTime.now()));
            messageService.SaveMessage(msg);
            buffer.write(msg + "\n");
            buffer.flush();
        }

        buffer.close();
        writer.close();
        output.close();
        socket.close();
    }
    //endregion

    //region Listen Getter/Setter
    private synchronized void setListen(boolean value) {
        this.listen = value;
    }

    private synchronized boolean getListen() {
        return this.listen;
    }
    //endregion
}
