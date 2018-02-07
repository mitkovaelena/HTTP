package javache;

import javache.http.HttpSession;
import javache.http.HttpSessionImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.FutureTask;

public class Server {

    private int port;
    private ServerSocket server;
    private Application application;


    public Server(int port, Application application) {
        this.port = port;
        this.application = application;
    }

    public void run() throws IOException {
        this.server = new ServerSocket(this.port);
        this.server.setSoTimeout(WebConstraints.SOCKET_TIMEOUT_MILLISECONDS);

        HttpSession session = new HttpSessionImpl();
        this.application.setSession(session);

        while (true){
            try(Socket clientSocket = this.server.accept()) {
                clientSocket.setSoTimeout(WebConstraints.SOCKET_TIMEOUT_MILLISECONDS);
                System.out.println("Client connected: " + clientSocket.getPort());

                ConnectionHandler connectionHandler =
                        new ConnectionHandler(clientSocket, new RequestHandler(this.application));
                FutureTask<?> task = new FutureTask<>(connectionHandler, null);
                task.run();
            } catch (SocketTimeoutException e){
                System.out.println("Socket connection expired");
            }
        }
    }
}
