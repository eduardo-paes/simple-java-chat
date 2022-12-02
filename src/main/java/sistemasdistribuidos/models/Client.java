package sistemasdistribuidos.models;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final long serialVersionUID = 1L;
    private Socket socket;
    private OutputStream output;
    private Writer writer;
    private BufferedWriter buffer;
    private String name;
    private final String host;
    private final int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        System.out.print("Introduce your name: ");
        Scanner in = new Scanner(System.in);
        name = in.nextLine();
        socket = new Socket(this.host,this.port);
        output = socket.getOutputStream();
        writer = new OutputStreamWriter(output);
        buffer = new BufferedWriter(writer);
        buffer.write(name);
        buffer.flush();
    }

    public void sendMessage(String msg) throws IOException {
        if (checkForExit(msg)) {
            buffer.write("Disconnected.\r\n");
            exit();
        } else {
            buffer.write(name + ": " + msg + "\r\n");
        }
        buffer.flush();
    }

    public void writeMessage() throws IOException {
        System.out.println("Chat started!");
        Scanner in = new Scanner(System.in);
        String msg;

        while (true) {
            msg = in.nextLine();
            if (checkForExit(msg)) break;
            sendMessage(msg);
        }
        exit();
    }

    public void listenMessage() throws IOException {
        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);
        String msg = "";

        while(!checkForExit(msg))
        {
            if (bfr.ready()) {
                msg = bfr.readLine();
                if (msg.equals("Exit"))
                    msg = "Server disconnected.";
                System.out.println(msg);
            }
        }
    }

    public void exit() throws IOException{
        buffer.close();
        writer.close();
        output.close();
        socket.close();
    }

    private boolean checkForExit(String msg) {
        return "Exit".equalsIgnoreCase(msg);
    }
}
