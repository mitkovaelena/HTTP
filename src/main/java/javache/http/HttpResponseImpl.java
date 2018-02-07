package javache.http;

import javache.WebConstraints;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseImpl implements HttpResponse {
    private HttpStatus statusCode;

    private HashMap<String, String> headers;

    private byte[] content;

    public HttpResponseImpl() {
        this.setContent(new byte[0]);
        this.headers = new HashMap<>();
    }

    private byte[] getHeadersBytes() {
        StringBuilder result = new StringBuilder()
                .append(ResponseLines.getResponseLine(this.getStatusCode().getStatusCode())).append(System.lineSeparator());

        for (Map.Entry<String,String> header : this.getHeaders().entrySet()) {
            result.append(header.getKey()).append(header.getValue()).append(System.lineSeparator());
        }

        result.append(System.lineSeparator());

        return result.toString().getBytes();
    }

    @Override
    public HashMap<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public HttpStatus getStatusCode() {
        return this.statusCode;
    }

    @Override
    public void setStatusCode(HttpStatus status) {
        this.statusCode = status;
    }

    @Override
    public byte[] getContent() {
        return this.content;
    }

    @Override
    public void setContent(byte[] content) {
        this.content = content;
    }

    @Override
    public void addHeader(String header, String value) {
        this.headers.putIfAbsent(header, value);
    }

    @Override
    public void addCookie(String cookie, String value) {
        String cookieString = cookie + "=" + value;
        if (!this.headers.containsKey(WebConstraints.SET_COOKIE_HEADER)) {
            this.headers.put(WebConstraints.SET_COOKIE_HEADER, cookieString);
        } else {
            this.headers.put(WebConstraints.SET_COOKIE_HEADER, this.headers.get(WebConstraints.SET_COOKIE_HEADER) + "; " + cookie);
        }
    }

    @Override
    public byte[] getBytes() {
        byte[] headersBytes = this.getHeadersBytes();
        byte[] bodyBytes = this.getContent();

        byte[] fullResponse = new byte[headersBytes.length + bodyBytes.length];

        System.arraycopy(headersBytes, 0, fullResponse, 0, headersBytes.length);
        System.arraycopy(bodyBytes, 0, fullResponse, headersBytes.length, bodyBytes.length);

        return fullResponse;
    }
}
