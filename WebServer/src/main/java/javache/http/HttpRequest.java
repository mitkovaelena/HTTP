package javache.http;

import java.util.HashMap;
import java.util.Map;

public interface HttpRequest {

    Map<String, String> getHeaders();

    Map<String, String> getBodyParameters();

    Map<String, HttpCookie> getCookies();

    String getMethod();

    void setMethod(String method);

    String getRequestUrl();

    void setRequestUrl(String requestUrl);

    void addHeader(String header, String value);

    void addBodyParameter(String parameter, String value);
}
