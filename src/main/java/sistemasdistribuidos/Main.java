package sistemasdistribuidos;

import sistemasdistribuidos.factories.AsynchronousFactory;
import sistemasdistribuidos.factories.SynchronousFactory;
import sistemasdistribuidos.interfaces.IMessageService;
import sistemasdistribuidos.interfaces.ISynchronousService;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            IMessageService messageService = AsynchronousFactory.getMessageService();
            if (Integer.parseInt(args[0]) == 1) {

            }
            else {
                messageService.SaveMessage("Início da mensagem.");
                messageService.SaveMessage("Mensagem 1.");
                messageService.SaveMessage("Mensagem 2.");
                messageService.SaveMessage("Mensagem 3.");
                messageService.LoadMessages();
                try {
                    System.in.read();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                messageService.ClearMessages();
                System.out.println("Terminou!");
            }
        }
        else {
            System.out.println("No parameters informed.");
        }

        /*
        ISynchronousService synchronousService = SynchronousFactory.getSynchronousService();
        synchronousService.Execute(args);
        * */
    }
}
