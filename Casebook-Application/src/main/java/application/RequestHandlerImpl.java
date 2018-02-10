package application;

import javache.Application;
import javache.RequestHandler;
import javache.WebConstants;
import javache.http.*;
import javache.io.Reader;
import javache.io.Writer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class RequestHandlerImpl implements RequestHandler {
    private HttpContext httpContext;
    private boolean hasIntercepted;
    private Application application;

    RequestHandlerImpl(Application application, HttpContext httpContext) {
        this.application = application;
        this.httpContext = httpContext;
        hasIntercepted = false;
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {
        String requestContent = null;

        for (int i = 0; i < WebConstants.SOCKET_TIMEOUT_MILLISECONDS; i++) {
            try {
                requestContent = Reader.readAllLines(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (requestContent.length() > 0) {
                break;
            }
        }

        HttpRequest httpRequest = new HttpRequestImpl(requestContent);
        HttpResponse httpResponse = new HttpResponseImpl();

        this.httpContext = new HttpContextImpl(httpRequest, httpResponse);

        try {
            Writer.writeBytes(this.application.handleRequest(this.httpContext), outputStream);
            hasIntercepted = true;
        } catch (Exception e){
            hasIntercepted = false;
        }

    }

    @Override
    public boolean hasIntercepted() {
        return hasIntercepted;
    }
}
