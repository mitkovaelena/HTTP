package javache;

import javache.http.HttpContext;
import javache.http.HttpSession;
import javache.http.HttpSessionStorage;

public interface Application {
    byte[] handleRequest(HttpContext httpContext);

    HttpSessionStorage getSessionStorage();

    void setSessionStorage(HttpSessionStorage sessionStorage);
}