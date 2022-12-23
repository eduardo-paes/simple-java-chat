package sistemasdistribuidos.interfaces;

import java.util.Scanner;

public interface IMessageService {
    void loadMessages();
    Scanner getReader();
    void saveMessage(String message);
    void clearMessages();
}
