package javache;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebConstraints {
    public static final String SERVER_HEADER = "Server: ";
    public static final String DATE_HEADER = "Date: ";
    public static final String CONTENT_TYPE_HEADER = "Content-Type: ";
    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition: ";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length: ";
    public static final String SET_COOKIE_HEADER = "Set-Cookie: ";
    public static final String LOCATION_HEADER = "Location: ";

    public static final String CONTENT_DISPOSITION_VALUE_INLINE = "inline";
    public static final String SERVER_NAME_AND_VERSION = "Javache/-1.0.0";
    public static final String SERVER_HTTP_VERSION = "HTTP/1.1";
    public static final int DEFAULT_PORT = 8000;


    public final static String RESOURCES_PATH =  System.getProperty("user.dir") +"/src/resources/";
    public final static String ASSETS_PATH = WebConstraints.RESOURCES_PATH + "assets/";
    public final static String USERS_DB_PATH = WebConstraints.RESOURCES_PATH + "db/users.txt";
    public final static String PAGES_PATH =  WebConstraints.RESOURCES_PATH + "pages/";

    public static final String INDEX_PAGE = "/html/index.html";
    public static final String LOGIN_PAGE = "/html/login.html";

    public static final String INDEX_ROUTE = "/";
    public static final String REGISTER_ROUTE = "/users/register";
    public static final String LOGIN_ROUTE = "/users/login";
    public static final String LOGOUT_ROUTE = "/users/logout";
    public static final String PROFILE_ROUTE = "/users/profile";

    public WebConstraints() { }
}