package sistemasdistribuidos.interfaces;

public interface IMessageService {
    void LoadMessages();
    void SaveMessage(String message);
    void ClearMessages();
}
