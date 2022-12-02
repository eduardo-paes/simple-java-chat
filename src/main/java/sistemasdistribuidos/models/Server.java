package sistemasdistribuidos.models;

import sistemasdistribuidos.factories.AsynchronousFactory;
import sistemasdistribuidos.interfaces.IMessageService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Server extends Thread {
    private static ArrayList<BufferedWriter> clients;
    private final Socket sConnection;
    private String name;
    private BufferedReader bReader;
    private final IMessageService messageService;

    public Server(Socket connection) {
        this.sConnection = connection;
        this.messageService = AsynchronousFactory.getMessageService();

        try {
            InputStream in = connection.getInputStream();
            InputStreamReader inr = new InputStreamReader(in);
            bReader = new BufferedReader(inr);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            OutputStream output =  this.sConnection.getOutputStream();
            Writer writer = new OutputStreamWriter(output);
            BufferedWriter buffer = new BufferedWriter(writer);
            clients.add(buffer);

            String msg = new String();
            //name = msg = bReader.readLine();

            while(msg != null && !checkForExit(msg))
            {
                msg = bReader.readLine();
                messageService.SaveMessage(String.format("[%s] - %s", getDate(), msg));
                sendToAll(buffer, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
        for (BufferedWriter bw : clients){
            if (!(bwSaida == bw)){
                bw.write(msg+"\r\n");
                bw.flush();
            }
        }
    }

    public static void start(int port) {
        try {
            System.out.println("Starting server.");
            ServerSocket sSocket = new ServerSocket(port);
            clients = new ArrayList<>();
            System.out.println("Server running at port: " + port);

            while (true) {
                System.out.println("Waiting connection...");
                Socket con = sSocket.accept();
                System.out.println("Client connected...");
                Thread t = new Server(con);
                t.start();
            }
        } catch (IOException e) {
            System.out.println("An error occurred opening server connection.");
            e.printStackTrace();
        }
    }

    private static boolean checkForExit(String msg) {
        return "Exit".equalsIgnoreCase(msg);
    }

    private static String getDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return dtf.format(LocalDateTime.now());
    }
}
