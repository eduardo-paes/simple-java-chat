package sistemasdistribuidos.factories;

import sistemasdistribuidos.services.SynchronousService;

public class SynchronousFactory {
    public static SynchronousService getSynchronousService(){
        return new SynchronousService();
    }
}
