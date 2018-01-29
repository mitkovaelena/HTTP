package javache.http;

import javache.WebConstraints;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestImpl implements HttpRequest {
    private String method;
    private String requestUrl;
    private Map<String, String> headers;
    private Map<String, String> bodyParameters;
    private boolean isResource;

    public HttpRequestImpl(String requestString) {
        this.method = this.extractRequestMethod(requestString);
        this.requestUrl = this.extractRequestUrl(requestString);
        this.headers = this.extractHeaders(requestString);
        this.bodyParameters = this.extractBodyParameters(requestString);
    }

    private Map<String,String> extractBodyParameters(String requestString) {
        Map<String, String> bodyParameters = new HashMap<>();
        String params = requestString.substring(requestString.indexOf("\r\n\r\n"));
        if(!params.trim().isEmpty()) {
            for (String pairStr : params.split("&")) {
                String[] pair = pairStr.split("=");
                bodyParameters.put(pair[0], pair[1]);
            }
        }
        return bodyParameters;
    }

    private Map<String,String> extractHeaders(String requestString) {
        Map<String, String> headers = new HashMap<>();
        String[] params = requestString.split("\r\n\r\n")[0].split("\r\n");

        for(int i = 1;  i < params.length; i++){
            String[] pair = params[i].split(": ");
            headers.put(pair[0], pair[1]);
        }
        return headers;
    }

    private String extractRequestUrl(String requestContent) {
        if(requestContent.split("\\s").length > 1) {
            String resourceUrl = requestContent.split("\\s")[1];
            this.isResource = resourceUrl.contains(".");
            return resourceUrl;
        }

        this.isResource = false;
        return requestContent;
    }

    private String extractRequestMethod(String requestContent) {
        if (requestContent.split("\\s").length > 0) {
            return requestContent.split("\\s")[0];
        }

        return requestContent;
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public Map<String, String> getBodyParameters() {
        return this.bodyParameters;
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getRequestUrl() {
        return this.requestUrl;
    }

    @Override
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public void addHeader(String header, String value) {
        this.headers.putIfAbsent(header, value);
    }

    @Override
    public void addBodyParameter(String parameter, String value) {
        this.bodyParameters.putIfAbsent(parameter, value);
    }

    @Override
    public boolean isResource() {
        return this.isResource;
    }
}
