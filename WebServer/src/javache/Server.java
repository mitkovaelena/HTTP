package javache;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.FutureTask;

public class Server {

    private int port;
    private ServerSocket serverSocket;
    private ServerConfig serverConfig;
    private RequestHandlerLoader requestHandlerLoader;


    public Server(int port, RequestHandlerLoader requestHandlerLoader) {
        this.port = port;
        this.requestHandlerLoader = requestHandlerLoader;
        this.serverConfig = new ServerConfig();
    }

    public void run() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        this.serverSocket.setSoTimeout(WebConstants.SOCKET_TIMEOUT_MILLISECONDS);
        this.serverConfig.initializeConfig();

        while (true){
            try(Socket clientSocket = this.serverSocket.accept()) {
                clientSocket.setSoTimeout(WebConstants.SOCKET_TIMEOUT_MILLISECONDS);
                System.out.println("Client connected: " + clientSocket.getPort());

                ConnectionHandler connectionHandler =
                        new ConnectionHandler(clientSocket, this.requestHandlerLoader.getRequestHandlers(), this.serverConfig.getHandlers());
                FutureTask<?> task = new FutureTask<>(connectionHandler, null);
                task.run();
            } catch (SocketTimeoutException e){
                System.out.println("Socket connection expired");
            }
        }
    }
}
