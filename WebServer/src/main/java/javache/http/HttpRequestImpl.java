package javache.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class HttpRequestImpl implements HttpRequest {
    private String method;

    private String requestUrl;

    private Map<String, String> headers;

    private Map<String, String> bodyParameters;

    private Map<String, HttpCookie> cookies;

    public HttpRequestImpl(String requestContent) {
        this.initMethod(requestContent);
        this.initRequestUrl(requestContent);
        this.initHeaders(requestContent);
        this.initBodyParameters(requestContent);
        this.initCookies();
    }

    private void initCookies() {
        this.cookies = new HashMap<>();
        if (!this.headers.containsKey("Cookie")) {
            return;
        }

        String cookieHeader = this.headers.get("Cookie");
        String[] cookiePairs = cookieHeader.split("; ");
        for (String cookiePair : cookiePairs) {
            String[] pair = cookiePair.split("=");
            this.cookies.put(pair[0], new HttpCookieImpl(pair[0], pair[1]));
        }
    }

    private void initMethod(String requestContent) {
        this.setMethod(requestContent.split("\\s")[0]);
    }

    private void initRequestUrl(String requestContent) {
        this.setRequestUrl(requestContent.split("\\s")[1]);
    }

    private void initHeaders(String requestContent) {
        this.headers = new HashMap<>();

        List<String> requestParams = Arrays.asList(
                requestContent.split("\\r\\n"));

        int i = 1;

        while(i < requestParams.size() && requestParams.get(i).length() > 0) {
            String[] headerKeyValuePair = requestParams.get(i).split("\\:\\s");

            this.addHeader(headerKeyValuePair[0], headerKeyValuePair[1]);

            i++;
        }
    }

    private void initBodyParameters(String requestContent) {
        if(this.getMethod().equals("POST")) {
            this.bodyParameters = new HashMap<>();

            List<String> requestParams = Arrays.asList(requestContent.split("\\r\\n"));

            if(requestParams.size() > this.headers.size() + 2) {
                List<String> bodyParams = Arrays.asList(requestParams.get(this.headers.size() + 2).split("\\&"));

                for (String bodyParam : bodyParams) {
                    String[] bodyKeyValuePair = bodyParam.split("\\=");

                    try {
                        this.addBodyParameter(bodyKeyValuePair[0], URLDecoder.decode(bodyKeyValuePair[1], "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(this.headers);
    }

    @Override
    public Map<String, String> getBodyParameters() {
        return Collections.unmodifiableMap(this.bodyParameters);
    }

    @Override
    public Map<String, HttpCookie> getCookies() {
        return Collections.unmodifiableMap(this.cookies);
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
}
