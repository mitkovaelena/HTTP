package javache;

import javache.http.HttpSession;
import javache.http.HttpSessionImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.FutureTask;

public class Server {
    private static final int SOCKET_TIMEOUT_MILLISECONDS = 10000;

    private int port;
    private ServerSocket server;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws IOException {
        this.server = new ServerSocket(this.port);
        this.server.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

        HttpSession session = new HttpSessionImpl();

        while (true){
            try(Socket clientSocket = this.server.accept()) {
                clientSocket.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);
                System.out.println("Client connected: " + clientSocket.getPort());

                ConnectionHandler connectionHandler =
                        new ConnectionHandler(clientSocket, new RequestHandler(session));
                FutureTask<?> task = new FutureTask<>(connectionHandler, null);
                task.run();
            } catch (SocketTimeoutException e){
                System.out.println("Socket connection expired");
            }
        }
    }
}
