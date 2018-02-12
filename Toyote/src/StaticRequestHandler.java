import javache.AbstractRequestHandler;
import javache.RequestHandler;
import javache.http.*;
import javache.io.Reader;
import javache.io.Writer;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

public class StaticRequestHandler extends AbstractRequestHandler {
    private static final String STATIC_FOLDER = File.separator + "static";

    private static final String SERVER_NAME_AND_VERSION = "Javache/-1.0.0";
    private static final String SERVER_HEADER = "Server: ";
    private static final String DATE_HEADER = "Date: ";
    private static final String CONTENT_TYPE_HEADER = "Content-Type: ";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length: ";
    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition: ";
    private static final String CONTENT_DISPOSITION_VALUE_INLINE = "inline";

    private boolean hasIntercepted;

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;


    public StaticRequestHandler(String serverRootPath) {
        super(serverRootPath);
        hasIntercepted = false;
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream) {
        try {
            String requestContent = Reader.readAllLines(inputStream);

            this.httpRequest = new HttpRequestImpl(requestContent);
            this.httpResponse = new HttpResponseImpl();

            String url = this.httpRequest.getRequestUrl();

            byte[] resourceData = this.getResource(url);
            if (resourceData == null) {
                hasIntercepted = false;
                return;
            }

            this.httpResponse.setContent(resourceData);
            this.setResponseHeaders();

            Writer.writeBytes(this.httpResponse.getBytes(), outputStream);
            hasIntercepted = true;

        } catch (IOException e) {
            e.printStackTrace();
            hasIntercepted = false;
        }
    }

    @Override
    public boolean hasIntercepted() {
        return hasIntercepted;
    }

    private byte[] getResource(String url) throws IOException {
        String pathName = this.serverRootPath + STATIC_FOLDER + url;
        File file = new File(pathName);


        byte[] fileByteData = null;

        if (file.exists() && !file.isDirectory()) {
            fileByteData = Files.readAllBytes(Paths.get(file.getCanonicalPath()));
            this.httpResponse.setStatusCode(HttpStatus.OK);
        }

        return fileByteData;
    }

    private void setResponseHeaders() throws IOException {
        this.httpResponse.addHeader(SERVER_HEADER, SERVER_NAME_AND_VERSION);
        this.httpResponse.addHeader(DATE_HEADER, new Date().toString());
        this.httpResponse.addHeader(CONTENT_TYPE_HEADER, this.getContentType(this.httpRequest.getRequestUrl()));
        this.httpResponse.addHeader(CONTENT_DISPOSITION_HEADER, CONTENT_DISPOSITION_VALUE_INLINE);
        this.httpResponse.addHeader(CONTENT_LENGTH_HEADER, String.valueOf(this.httpResponse.getContent().length));
    }

    private String getContentType(String resourceUrl) throws IOException {
        return Files.probeContentType(Paths.get(this.serverRootPath + STATIC_FOLDER + resourceUrl));
    }
}
