import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javache.AbstractRequestHandler;
import javache.RequestHandler;
import javache.http.HttpRequest;
import javache.http.HttpRequestImpl;
import javache.http.HttpResponse;
import javache.io.Reader;
import javache.io.Writer;

import solet.*;
import util.SoletLoader;

public class SoletDispatcher extends AbstractRequestHandler {

    private boolean hasIntercepted;
    private SoletLoader soletLoader;

    public SoletDispatcher(String serverRootPath) {
        super(serverRootPath);
        this.hasIntercepted = false;
        this.soletLoader = new SoletLoader(this.serverRootPath);
        this.soletLoader.loadSolets();
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {
        try {
            HttpSoletResponse response = new HttpSoletResponseImpl(outputStream);
            String requestContent = Reader.readAllLines(inputStream);
            HttpRequest request = new HttpRequestImpl(requestContent);

            for (HttpSolet solet: this.soletLoader.getSolets().values()) {
                String soletRoute = solet.getClass().getDeclaredAnnotation(WebSolet.class).route();

                if (soletRoute.equals(request.getRequestUrl())) {
                    solet.getClass().getMethod(
                            this.getMethodName(request.getMethod()), HttpSoletRequest.class, HttpSoletResponse.class)
                            .invoke(solet, new HttpSoletRequestImpl(requestContent, null), response);

                    Writer.writeBytes(response.getBytes(), outputStream);
                    this.hasIntercepted = true;
                }
            }
        } catch (ReflectiveOperationException | IOException e) {
            this.hasIntercepted = false;
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasIntercepted() {
        return this.hasIntercepted;
    }

    private String getMethodName(String method){
        return "do" + method.substring(0,1) + method.substring(1).toLowerCase();
    }

}
