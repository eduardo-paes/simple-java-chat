package sistemasdistribuidos.factories;

import sistemasdistribuidos.services.MessageService;

public class AsynchronousFactory {
    public static MessageService getMessageService() {
        return new MessageService();
    }
}
