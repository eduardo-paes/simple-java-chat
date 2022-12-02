package sistemasdistribuidos;

import sistemasdistribuidos.factories.AsynchronousFactory;
import sistemasdistribuidos.interfaces.IAsynchronousService;

public class Main {
    private static IAsynchronousService service;

    public static void main(String[] args) {
//        ISynchronousService synchronousService = SynchronousFactory.getSynchronousService();
//        synchronousService.Execute(args);

        AsynchronousMode(args);
    }

    private static void AsynchronousMode(String[] args){
        // Testing
        if (args.length > 0) {
            // Server
            service = AsynchronousFactory.getAsynchronousService();
            if (Integer.parseInt(args[0]) == 0) {
                service.StartServer();
            }
            // Client
            else {
                service.StartClient();
            }
        }
        else {
            System.out.println("No parameters informed.");
        }
    }
}
