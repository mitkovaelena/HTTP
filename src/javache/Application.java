package javache;

import javache.http.HttpContext;
import javache.http.HttpSession;

public interface Application {
    byte[] handleRequest(HttpContext httpContext);
    void setSession(HttpSession session);
}