package javache;

import javache.io.Reader;
import javache.io.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler extends Thread {
    private Socket clientSocket;
    private InputStream csInputStream;
    private OutputStream csOutputStream;
    private RequestHandler requestHandler;

    public ConnectionHandler(Socket clientSocket, RequestHandler requestHandler) {
        this.initializeConnection(clientSocket);
        this.requestHandler = requestHandler;
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
            String requestContent = Reader.readAllLines(this.csInputStream);
            byte[] responseContent = this.requestHandler.handleRequest(requestContent);
            Writer.writeBytes(responseContent, this.csOutputStream);
            csInputStream.close();
            csOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
