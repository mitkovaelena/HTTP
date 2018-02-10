package javache;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler extends Thread {
    private Socket clientSocket;
    private InputStream csInputStream;
    private OutputStream csOutputStream;
    private Iterable<RequestHandler> requestHandlers;

    public ConnectionHandler(Socket clientSocket, Iterable<RequestHandler> requestHandlers) {
        this.initializeConnection(clientSocket);
        this.requestHandlers = requestHandlers;
    }

    private void initializeConnection(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            this.csInputStream = this.clientSocket.getInputStream();
            this.csOutputStream = this.clientSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            for (RequestHandler requestHandler : this.requestHandlers) {
                requestHandler.handleRequest(csInputStream, csOutputStream);

                if(requestHandler.hasIntercepted()){
                    break;
                }
            }

            csInputStream.close();
            csOutputStream.close();
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
