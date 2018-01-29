package javache.http;

import javache.WebConstraints;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseImpl implements HttpResponse {
    private Map<String, String> headers;
    private int statusCode;
    byte[] content;
    private HashMap<Integer, String> responseLines;


    public HttpResponseImpl() {
        this.responseLines = new HashMap<>();
        this.headers = new HashMap<>();

        seedResponseLines();
    }

    private void seedResponseLines() {
        this.responseLines.put(WebConstraints.OK_STATUS_CODE, WebConstraints.OK_STATUS);
        this.responseLines.put(WebConstraints.FOUND_STATUS_CODE, WebConstraints.FOUND_STATUS);
        this.responseLines.put(WebConstraints.BAD_REQUEST_STATUS_CODE, WebConstraints.BAD_REQUEST_STATUS);
        this.responseLines.put(WebConstraints.UNAUTHORIZED_STATUS_CODE, WebConstraints.UNAUTHORIZED_STATUS);
        this.responseLines.put(WebConstraints.NOT_FOUND_CODE, WebConstraints.NOT_FOUND_STATUS);
        this.responseLines.put(WebConstraints.INTERNAL_SERVER_ERROR_STATUS_CODE, WebConstraints.INTERNAL_SERVER_ERROR_STATUS);

    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    @Override
    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public byte[] getBytes() {
        byte[] headersAsBytes = this.getHeadersAsBytes();
        byte[] fullResponseByteData = new byte[headersAsBytes.length + content.length];

        for (int i = 0; i < headersAsBytes.length; i++) {
            fullResponseByteData[i] = headersAsBytes[i];
        }

        for (int i = 0; i < content.length; i++) {
            fullResponseByteData[i + headersAsBytes.length] = content[i];
        }

        return fullResponseByteData;
    }

    private byte[] getHeadersAsBytes() {
        //Construct first row
        StringBuilder responseHeaders = new StringBuilder()
                .append(WebConstraints.HTTP_VERSION).append(" ")
                .append(this.statusCode).append(" ")
                .append(this.responseLines.get(this.statusCode))
                .append(System.lineSeparator());

        //Append other headers
        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseHeaders.append(header.getKey())
                    .append(header.getValue())
                    .append(System.lineSeparator());
        }
        responseHeaders.append(System.lineSeparator());
        return  responseHeaders.toString().getBytes();
    }

    @Override
    public void addHeader(String header, String value) {
        this.headers.putIfAbsent(header, value);
    }

}
