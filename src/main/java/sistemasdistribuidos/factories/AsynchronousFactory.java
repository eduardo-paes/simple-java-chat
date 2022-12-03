package sistemasdistribuidos.factories;

import sistemasdistribuidos.services.AsynchronousService;
import sistemasdistribuidos.services.MessageService;

public class AsynchronousFactory {
    public static MessageService getMessageService(String msgFileName) {
        return new MessageService(msgFileName);
    }
    public static AsynchronousService getAsynchronousService() {
        return new AsynchronousService();
    }
}
