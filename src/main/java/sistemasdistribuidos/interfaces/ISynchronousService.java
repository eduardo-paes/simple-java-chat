package sistemasdistribuidos.interfaces;

public interface ISynchronousService {
    void ClientMode();
    void ServerMode();

    void Execute(String[] args);
}
