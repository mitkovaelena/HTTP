package javache;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

public class RequestHandler {
    private String resourceExtension;
    private int resourceSize;
    private String resourceStatus;
    private HashMap<String, String> responseLines;
    private HashMap<String, String> supportedContentTypes;

    public RequestHandler() {
        this.supportedContentTypes = new HashMap<>();
        this.responseLines = new HashMap<>();
        this.seedSupportedContentTypes();
        this.seedResponseLines();
    }

    private void seedSupportedContentTypes() {
        this.supportedContentTypes.put("png", "image/png");
        this.supportedContentTypes.put("jpg", "image/jpeg");
        this.supportedContentTypes.put("jpeg", "image/jpeg");
        this.supportedContentTypes.put("html", "text/html");
    }

    private void seedResponseLines() {
        this.responseLines.put("ok", "HTTP/1.1 200 OK");
        this.responseLines.put("found", "HTTP/1.1 302 Found");
        this.responseLines.put("bad_request", "HTTP/1.1 400 Bad Request");
        this.responseLines.put("unathorized", "HTTP/1.1 401 Unathorized");
        this.responseLines.put("not_found", "HTTP/1.1 404 Not Found");
        this.responseLines.put("server_error", "HTTP/1.1 500 Internal Server Error");
    }

    public byte[] handleRequest(String requestContent) {
        String requestMethod = this.extractRequestMethod(requestContent);
        String requestResource = this.extractRequestResource(requestContent);

        if (requestMethod.equals("GET")) {
            byte[] resourceData = this.getResource(requestResource);
            byte[] responseContent = this.constructResponse(requestContent, resourceData);
            return responseContent;
        }

        return null;
    }

    private byte[] getResource(String requestResource) {
        byte[] fileByteData = null;
        try {
            this.resourceExtension = requestResource.substring(requestResource.lastIndexOf(".") + 1);

            String resourcesSubfolderPath = "";
            if(!requestResource.contains(WebConstraints.PAGES_PATH) && !requestResource.contains(WebConstraints.ASSETS_PATH)) {
                if (this.resourceExtension.equals("html")) {
                    resourcesSubfolderPath = "\\" + WebConstraints.PAGES_PATH;
                } else if (this.supportedContentTypes.containsKey(this.resourceExtension)) {
                    resourcesSubfolderPath = "\\" + WebConstraints.ASSETS_PATH;
                }
            }

            fileByteData = Files.readAllBytes(Paths.get(WebConstraints.RESOURCES_PATH + resourcesSubfolderPath + requestResource));
            this.resourceSize = fileByteData.length;
            this.resourceStatus = "ok";
        } catch (NoSuchFileException e) {
            this.resourceStatus = "not_found";
        } catch (AccessDeniedException e) {
            this.resourceStatus = "unauthorized";
        } catch (IOException e) {
            this.resourceStatus = "server_error";
            e.printStackTrace();
        }

        return fileByteData;
    }

    private String extractRequestResource(String requestContent) {
        if(requestContent.split("\\s").length > 1) {
            return requestContent.split("\\s")[1];
        }

        return requestContent;
    }

    private String extractRequestMethod(String requestContent) {
        if(requestContent.split("\\s").length > 0) {
            return requestContent.split("\\s")[0];
        }

        return requestContent;
    }

    private byte[] constructResponse(String requestContent, byte[] requestResult) {
        String responseHeaders = this.getResponseHeaders(requestContent);

        byte[] headersAsBytes = responseHeaders.getBytes();

        byte[] fullResponseByteData = new byte[headersAsBytes.length + requestResult.length];

        for (int i = 0; i < headersAsBytes.length; i++) {
            fullResponseByteData[i] = headersAsBytes[i];
        }

        for (int i = 0; i < requestResult.length; i++) {
            fullResponseByteData[i + headersAsBytes.length] = requestResult[i];
        }

        return fullResponseByteData;
    }

    private String getResponseHeaders(String requestContent) {
        StringBuilder resultHeaders = new StringBuilder()
                .append(this.responseLines.get(this.resourceStatus)).append(System.lineSeparator())
                .append(WebConstraints.SERVER_HEADER_NAME_AND_VERSION).append(System.lineSeparator())
                .append(WebConstraints.DATE_HEADER).append(this.getNewDate()).append(System.lineSeparator());

        if(this.verifyResourceStatus()) {
            resultHeaders
                    .append(WebConstraints.CONTENT_TYPE_HEADER).append(this.getContentType(this.resourceExtension)).append(System.lineSeparator())
                    .append(WebConstraints.CONTENT_DISPOSITION_HEADER).append(WebConstraints.CONTENT_DISPOSITION_VALUE_INLINE).append(System.lineSeparator())
                    .append(WebConstraints.CONTENT_LENGTH_HEADER).append(this.resourceSize).append(System.lineSeparator());

        } else if(this.resourceStatus.equals("found")){
            resultHeaders.append(WebConstraints.LOCATION_HEADER).append(WebConstraints.DEFAULT_PAGE).append(System.lineSeparator());
        }

        return resultHeaders.append(System.lineSeparator()).toString();
    }

    private String getContentType(String resourceExtension) {
        if (this.supportedContentTypes.containsKey(resourceExtension)) {
            return this.supportedContentTypes.get(resourceExtension);
        }

        return "text/plain";
    }

    private boolean verifyResourceStatus() {
        return this.resourceStatus.equals("ok");
    }

    private Date getNewDate() {
        return new Date();
    }


}
