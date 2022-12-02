package sistemasdistribuidos.factories;

import sistemasdistribuidos.services.AsynchronousService;
import sistemasdistribuidos.services.MessageService;

public class AsynchronousFactory {
    public static MessageService getMessageService() {
        return new MessageService();
    }
    public static AsynchronousService getAsynchronousService() {
        return new AsynchronousService();
    }
}
