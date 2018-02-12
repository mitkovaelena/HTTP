package javache;

import javache.io.Reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

public class ConnectionHandler extends Thread {
    private Socket clientSocket;
    private InputStream csInputStream;
    private OutputStream csOutputStream;
    private Map<String, RequestHandler> requestHandlers;
    private Set<String> requestHandlersByPriority;
    private String cachedStringContent;

    public ConnectionHandler(Socket clientSocket, Map<String, RequestHandler> requestHandlers, Set<String> requestHandlersByPriority) {
        this.initializeConnection(clientSocket);
        this.requestHandlers = requestHandlers;
        this.requestHandlersByPriority = requestHandlersByPriority;
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

    private InputStream getClientSocketInputStream() throws IOException {
        if (this.cachedStringContent == null) {
            this.cachedStringContent = Reader.readAllLines(this.csInputStream);
        }
        return new ByteArrayInputStream(cachedStringContent.getBytes());
    }

    @Override
    public void run() {
        try {
            for (String requestHandlerName : this.requestHandlersByPriority) {
                if(requestHandlers.containsKey(requestHandlerName)) {
                    RequestHandler requestHandler = requestHandlers.get(requestHandlerName);
                    requestHandler.handleRequest(this.getClientSocketInputStream(), csOutputStream);

                    if (requestHandler.hasIntercepted()) {
                        break;
                    }
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
