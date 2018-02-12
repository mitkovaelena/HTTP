package solet;

import javache.http.HttpResponseImpl;
import java.io.OutputStream;

public class HttpSoletResponseImpl extends HttpResponseImpl implements HttpSoletResponse {
    private OutputStream responseStream;

    public HttpSoletResponseImpl(OutputStream responseStream) {
        super();
        this.responseStream = responseStream;
    }


    @Override
    public OutputStream getResponseStream() {
        return this.responseStream;
    }
}
