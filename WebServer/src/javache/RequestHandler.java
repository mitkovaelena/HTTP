package javache;

import java.io.InputStream;
import java.io.OutputStream;

public interface RequestHandler {
    void handleRequest(InputStream inputStream, OutputStream outputStream);

    boolean hasIntercepted();
}
