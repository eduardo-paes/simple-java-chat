package sistemasdistribuidos.interfaces;

public interface IAsynchronousService {
    void StartClient();
    void StartServer();
    void Execute(String[] args);
}
