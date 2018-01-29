package javache;

import javache.http.HttpRequest;
import javache.http.HttpRequestImpl;
import javache.http.HttpResponse;
import javache.http.HttpResponseImpl;

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
    private HashMap<String, String> supportedContentTypes;

    public RequestHandler() {
        this.supportedContentTypes = new HashMap<>();
        this.seedSupportedContentTypes();
    }

    public byte[] handleRequest(String requestContent) {
        this.httpRequest = new HttpRequestImpl(requestContent);
        this.httpResponse = new HttpResponseImpl();

        byte[] resourceData = this.getResource(this.httpRequest.getRequestUrl());
        this.httpResponse.setContent(resourceData);
        this.setResponseHeaders();

        return httpResponse.getBytes();
    }

    private byte[] getResource(String requestResource) {
        byte[] fileByteData = null;
        try {
            String returnPath = "";
            if (!this.httpRequest.isResource()) {
                returnPath = WebConstraints.RESOURCES_PATH + WebConstraints.PAGES_PATH + requestResource + ".html";
            } else {
                returnPath = WebConstraints.RESOURCES_PATH + WebConstraints.ASSETS_PATH + requestResource;
            }

            fileByteData = Files.readAllBytes(Paths.get(returnPath));
            this.httpResponse.setStatusCode(WebConstraints.OK_STATUS_CODE);
        } catch (NoSuchFileException e) {
            this.httpResponse.setStatusCode(WebConstraints.NOT_MODIFIED_STATUS_CODE);
        } catch (AccessDeniedException e) {
            this.httpResponse.setStatusCode(WebConstraints.UNAUTHORIZED_STATUS_CODE);
        } catch (IOException e) {
            this.httpResponse.setStatusCode(WebConstraints.INTERNAL_SERVER_ERROR_STATUS_CODE);
            e.printStackTrace();
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
        this.supportedContentTypes.put("html", "text/html");
    }

    private String getContentType(String resourceUrl) {
        String resourceExtension = resourceUrl.substring(resourceUrl.lastIndexOf(".") + 1);
        if (this.supportedContentTypes.containsKey(resourceExtension)) {
            return this.supportedContentTypes.get(resourceExtension);
        }

        return "text/html";
    }

    private boolean verifyResourceStatus() {
        return this.httpResponse.getStatusCode() == 200;
    }
}
