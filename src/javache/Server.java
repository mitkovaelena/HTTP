package javache;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.FutureTask;

public class Server {
    private static final int SOCKET_TIMEOUT_MILLISECONDS = 1000;

    private int port;
    private ServerSocket server;

    public Server(int port) {
        this.port = port;
    }

    public void run() throws IOException {
        this.server = new ServerSocket(this.port);
        this.server.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);

        System.out.println("Listening on port: " + this.port);

        while (true){
            try(Socket clientSocket = this.server.accept()) {
                clientSocket.setSoTimeout(SOCKET_TIMEOUT_MILLISECONDS);
                System.out.println("Client connected: " + clientSocket.getPort());

                ConnectionHandler connectionHandler =
                        new ConnectionHandler(clientSocket, new RequestHandler());
                FutureTask<?> task = new FutureTask<>(connectionHandler, null);
                task.run();
            } catch (SocketTimeoutException ste){
                System.out.println("Socket connection expired");
            }
        }
    }
}
