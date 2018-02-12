package solet;

import javache.http.HttpRequest;
import javache.http.HttpResponse;

import java.io.InputStream;
import java.io.OutputStream;

public interface HttpSoletResponse extends HttpResponse {

    OutputStream getResponseStream();
}
