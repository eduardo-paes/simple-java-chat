package sistemasdistribuidos.services;

import sistemasdistribuidos.interfaces.IMessageService;

import java.io.*;
import java.util.Scanner;

public class MessageService implements IMessageService {
    private final String msgFileName;

    public MessageService(String msgFileName) {
        String directoryMessages = "messages/";
        this.msgFileName = directoryMessages + msgFileName;
    }

    protected void CreateMessageFile() {
        try {
            File msgFile = new File(msgFileName);
            if (msgFile.createNewFile()) {
                System.out.println("Backup message created.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred creating file.");
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void LoadMessages() {
        try {
            File msgFile = new File(msgFileName);
            if (msgFile.exists()){
                Scanner reader = new Scanner(msgFile);
                while (reader.hasNextLine()) {
                    String data = reader.nextLine();
                    System.out.println(data);
                }
                reader.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred loading messages.");
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void SaveMessage(String message) {
        // Create file if it doesn't exist
        CreateMessageFile();

        // Save message received from client to file
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(msgFileName, true));
            writer.println(message);
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred saving message.");
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void ClearMessages() {
        File msgFile = new File(msgFileName);
        if (!msgFile.delete()) {
            System.out.println("Failed to delete messages.");
        }
    }
}
