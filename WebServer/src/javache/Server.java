package javache;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.FutureTask;

public class Server {

    private int port;
    private ServerSocket serverSocket;
    private Iterable<RequestHandler> requestHandlers;


    public Server(int port, Iterable<RequestHandler> requestHandlers) {
        this.port = port;
        this.requestHandlers = requestHandlers;
    }

    public void run() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        this.serverSocket.setSoTimeout(WebConstants.SOCKET_TIMEOUT_MILLISECONDS);

        //HttpSessionStorage sessionStorage = new HttpSessionStorageImpl();
        //this.application.setSessionStorage(sessionStorage);

        while (true){
            try(Socket clientSocket = this.serverSocket.accept()) {
                clientSocket.setSoTimeout(WebConstants.SOCKET_TIMEOUT_MILLISECONDS);
                System.out.println("Client connected: " + clientSocket.getPort());

                ConnectionHandler connectionHandler =
                        new ConnectionHandler(clientSocket, this.requestHandlers);
                FutureTask<?> task = new FutureTask<>(connectionHandler, null);
                task.run();
            } catch (SocketTimeoutException e){
                System.out.println("Socket connection expired");
            }
        }
    }
}
