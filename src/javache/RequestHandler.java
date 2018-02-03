package javache;

import javache.http.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

public class RequestHandler {
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private HttpSession httpSession;
    private HashMap<String, String> supportedContentTypes;

    public RequestHandler(HttpSession httpSession) {
        this.supportedContentTypes = new HashMap<>();
        this.seedSupportedContentTypes();
        this.httpSession = httpSession;
    }

    public byte[] handleRequest(String requestContent) {
        this.httpRequest = new HttpRequestImpl(requestContent);
        this.httpResponse = new HttpResponseImpl();

        byte[] resourceData = null;

        String url = this.httpRequest.getRequestUrl();
        switch (url) {
            case "/":
                resourceData = this.getResource(WebConstraints.DEFAULT_PAGE);
                httpResponse.setStatusCode(HttpStatus.Ok);
                break;
            case "/users/register":
                resourceData = "<h1> I am register</h1>".getBytes();
                httpResponse.setStatusCode(HttpStatus.Ok);
                break;
            case "/users/login":
                resourceData = "<h1> I am login</h1>".getBytes();
                httpResponse.setStatusCode(HttpStatus.Ok);
                break;
            case "/users/profile":
                resourceData = "<h1> I am profile</h1>".getBytes();
                httpResponse.setStatusCode(HttpStatus.Ok);
                break;
            default:
                resourceData = this.getResource(url);
        }

        this.httpResponse.setContent(resourceData);
        this.setResponseHeaders();
        return httpResponse.getBytes();
    }

    private byte[] getResource(String url) {
        String pathName = WebConstraints.ASSETS_PATH + url;
        File file = new File(pathName);

        byte[] fileByteData = null;

        if (!file.exists() || file.isDirectory()) {
            this.httpResponse.setStatusCode(HttpStatus.NotFound);
        } else {

            try {
                if (!file.getCanonicalPath().startsWith(WebConstraints.ASSETS_PATH)) {
                    this.httpResponse.setStatusCode(HttpStatus.BadRequest);
                }

                fileByteData = Files.readAllBytes(Paths.get(pathName));
                this.httpResponse.setStatusCode(HttpStatus.Ok);

            } catch (AccessDeniedException e) {
                this.httpResponse.setStatusCode(HttpStatus.Unauthorized);
            } catch (IOException e) {
                this.httpResponse.setStatusCode(HttpStatus.InternalServerError);
                e.printStackTrace();
            }
        }
        return fileByteData;
    }

    private void setResponseHeaders() {
        this.httpResponse.addHeader(WebConstraints.SERVER_HEADER, WebConstraints.SERVER_NAME_AND_VERSION);
        this.httpResponse.addHeader(WebConstraints.DATE_HEADER, new Date().toString());

        if (this.verifyResourceStatus()) {
            this.httpResponse.addHeader(WebConstraints.CONTENT_TYPE_HEADER, this.getContentType(this.httpRequest.getRequestUrl()));
            this.httpResponse.addHeader(WebConstraints.CONTENT_DISPOSITION_HEADER, WebConstraints.CONTENT_DISPOSITION_VALUE_INLINE);
            this.httpResponse.addHeader(WebConstraints.CONTENT_LENGTH_HEADER, String.valueOf(this.httpResponse.getContent().length));
        }
    }

    private void seedSupportedContentTypes() {
        this.supportedContentTypes.put("png", "image/png");
        this.supportedContentTypes.put("jpg", "image/jpeg");
        this.supportedContentTypes.put("jpeg", "image/jpeg");
        this.supportedContentTypes.put("css", "text/css");
        this.supportedContentTypes.put("html", "text/html");
    }

    private String getContentType(String resourceUrl) {
        String resourceExtension = resourceUrl.substring(resourceUrl.lastIndexOf(".") + 1);
        if (this.supportedContentTypes.containsKey(resourceExtension)) {
            return this.supportedContentTypes.get(resourceExtension);
        }

        return "text/plain";
    }

    private boolean verifyResourceStatus() {
        return this.httpResponse.getStatusCode().getStatusCode() == 200;
    }

}
