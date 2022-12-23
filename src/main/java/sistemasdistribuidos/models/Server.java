package sistemasdistribuidos.models;

import sistemasdistribuidos.factories.AsynchronousFactory;
import sistemasdistribuidos.interfaces.IMessageService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

public class Server extends Thread {
    //region Attributes
    private static ArrayList<BufferedWriter> clients;
    private final Socket sConnection;
    private BufferedReader bReader;
    private boolean firstEntry;
    private String clientName;
    private final IMessageService messageService;
    //endregion

    //region Constructor
    public Server(Socket connection) {
        this.firstEntry = true;
        this.sConnection = connection;
        this.messageService = AsynchronousFactory.getMessageService("server_messages.txt");
        try {
            this.bReader = getBufferedReader(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region Methods
    public static void start(int port)  {
        try {
            System.out.println("Starting server.");
            ServerSocket sSocket = new ServerSocket(port);
            clients = new ArrayList<>();
            System.out.println("Server running at port: " + port);

            while (!sSocket.isClosed()) {
                System.out.println("Waiting connection...");
                Socket con = sSocket.accept();
                System.out.println("Client connected...");
                new Server(con).start();
            }
        } catch (IOException e) {
            System.out.println("An error occurred opening server connection.");
            e.printStackTrace();
        }
    }

    public void run() {
        BufferedWriter buffer = null;
        try {
            buffer = getBufferedWriter(this.sConnection);
            clients.add(buffer);

            // Send old messages from server to client
            syncClientMessages(buffer);

            String msg;
            while (!this.sConnection.isClosed()) {
                msg = bReader.readLine();
                if (msg != null) {
                    if (this.firstEntry) {
                        this.sConnection.setSoTimeout(500);
                        this.firstEntry = false;
                    }

                    if (msg.contains("!@#REC_DIS@!"))
                        continue;

                    if (this.clientName == null)
                        this.clientName = getClientName(msg);

                    messageService.saveMessage(msg);
                    if (msg.contains("Disconnecting..."))
                        clients.remove(buffer);

                    sendToAll(buffer, msg);
                }
            }
        } catch (SocketTimeoutException e) {
            sendDisconnectionMessage(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
        BufferedWriter toDelete = null;
        for (BufferedWriter bw : clients) {
            if (!(bwSaida == bw)) {
                try {
                    bw.write(msg + "\r\n");
                    bw.flush();
                } catch (Exception e) {
                    toDelete = bw;
                }
            }
        }
        if (toDelete != null) {
            clients.remove(toDelete);
        }
    }

    private void syncClientMessages(BufferedWriter buffer) throws IOException {
        Scanner reader = messageService.getReader();
        if (reader != null) {
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                buffer.write(data + "\r\n");
                buffer.flush();
            }
            reader.close();
        }
        buffer.write("END_SERVER_SYNC\r\n");
        buffer.flush();
    }

    private void sendDisconnectionMessage(BufferedWriter buffer) {
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            String msg = String.format("%s: Disconnecting... - [%s]", this.clientName, dtf.format(LocalDateTime.now()));
            messageService.saveMessage(msg);
            sendToAll(buffer, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getClientName(String text) {
        Optional<String> name = Arrays.stream(text.split(": ")).findFirst();
        return name.orElse("User");
    }

    private static BufferedWriter getBufferedWriter(Socket socket) throws IOException {
        OutputStream output = socket.getOutputStream();
        Writer writer = new OutputStreamWriter(output);
        return new BufferedWriter(writer);
    }

    private static BufferedReader getBufferedReader(Socket socket) throws IOException {
        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        return new BufferedReader(inr);
    }

    //endregion
}
