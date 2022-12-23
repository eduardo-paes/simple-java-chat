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
    private OutputStream pingOutput;
    private Writer writer;
    private Writer pingWriter;
    private BufferedWriter buffer;
    private BufferedWriter pingBuffer;
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
        pingOutput = socket.getOutputStream();
        writer = new OutputStreamWriter(socket.getOutputStream());
        pingWriter = new OutputStreamWriter(socket.getOutputStream());
        buffer = new BufferedWriter(writer);
        pingBuffer = new BufferedWriter(pingWriter);

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

        // Keep pinging server
        keepPinging();

        // Start to get messages from client to send to server
        writeMessage();
    }

    private void startChatMessages() {
        System.out.println("Chat started!\nLoading old messages...");

        // Synchronize messages with server
        try {
            String msg;
            BufferedReader bfr = getBufferedReader();
            this.messageService.clearMessages();
            while (true) {
                if (bfr.ready()) {
                    msg = bfr.readLine();
                    if (msg.contains("END_SERVER_SYNC")) break;
                    System.out.println(msg);
                    messageService.saveMessage(msg);
                }
            }
        } catch (Exception e) {
            this.messageService.loadMessages();
        }

        System.out.println("---");
    }

    private void listenMessage() {
        new Thread(() -> {
            try {
                BufferedReader bfr = getBufferedReader();

                String msg;
                while(getListen()) {
                    if (bfr.ready()) {
                        msg = bfr.readLine();
                        if (msg.contains("!@#CHK_DIS$@!")) {
                            buffer.write("!@#REC_DIS@!\n");
                            buffer.flush();
                            continue;
                        }

                        messageService.saveMessage(msg);
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
            messageService.saveMessage(msg);
            buffer.write(msg + "\n");
            buffer.flush();
        }

        buffer.close();
        writer.close();
        output.close();
        pingBuffer.close();
        pingWriter.close();
        pingOutput.close();
        socket.close();
    }

    private void keepPinging() {
        new Thread(() -> {
            try {
                while(getListen()) {
                    pingBuffer.write("!@#REC_DIS@!\n");
                    pingBuffer.flush();
                    Thread.sleep(250);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private BufferedReader getBufferedReader() throws IOException {
        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        return new BufferedReader(inr);
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
