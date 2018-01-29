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
    public static final String LOCATION_HEADER = "Location: ";

    public static final String CONTENT_DISPOSITION_VALUE_INLINE = "inline";
    public static final String SERVER_NAME_AND_VERSION = "Javache/-1.0.0";
    public static final String SERVER_HTTP_VERSION = "HTTP/1.1";

    public final static String RESOURCES_PATH = "E:\\SoftUni\\8.Web\\1.Basics\\HTTP\\src\\javache\\resources\\";
    public final static String ASSETS_PATH = "assets";
    public final static String PAGES_PATH = "pages";

    public static final String DEFAULT_PAGE = "/index.html";
    public static final int DEFAULT_PORT = 8000;

    public WebConstraints() { }
}