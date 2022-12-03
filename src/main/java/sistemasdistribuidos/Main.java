package sistemasdistribuidos;

import sistemasdistribuidos.factories.AsynchronousFactory;
import sistemasdistribuidos.factories.SynchronousFactory;
import sistemasdistribuidos.interfaces.IAsynchronousService;
import sistemasdistribuidos.interfaces.ISynchronousService;

public class Main {

    public static void main(String[] args) {
        SynchronousMode(args);
    }

    private static void AsynchronousMode(String[] args) {
        IAsynchronousService asynchronousService = AsynchronousFactory.getAsynchronousService();
        asynchronousService.Execute(args);
    }

    private static void SynchronousMode(String[] args) {
        ISynchronousService synchronousService = SynchronousFactory.getSynchronousService();
        synchronousService.Execute(args);
    }
}
