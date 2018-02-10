package javache.lib.handlers;

import javache.RequestHandler;
import javache.http.*;

import java.io.InputStream;
import java.io.OutputStream;

class RequestHandlerImpl implements RequestHandler {
    private HttpContext httpContext;
    private boolean hasIntercepted;
    //private Application application;

    RequestHandlerImpl() {
        //this.application = application;
        hasIntercepted = false;
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {
//        String requestContent = null;
//
//        for (int i = 0; i < WebConstants.SOCKET_TIMEOUT_MILLISECONDS; i++) {
//            requestContent = Reader.readAllLines(inputStream);
//
//            if (requestContent.length() > 0) {
//                break;
//            }
//        }

//        HttpRequest httpRequest = new HttpRequestImpl(requestContent);
//        HttpResponse httpResponse = new HttpResponseImpl();
//
//        this.httpContext = new HttpContextImpl(httpRequest, httpResponse);
//
//        try {
//            Writer.writeBytes(this.application.handleRequest(this.httpContext), outputStream);
//            hasIntercepted = true;
//        } catch (Exception e){
//            hasIntercepted = false;
//        }

    }

    @Override
    public boolean hasIntercepted() {
        return hasIntercepted;
    }
}
