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
    public static final String HTTP_VERSION = "HTTP/1.1";

    public final static String RESOURCES_PATH = "E:\\SoftUni\\8.Web\\1.Basics\\HTTP\\src\\javache\\resources\\";
    public final static String ASSETS_PATH = "assets";
    public final static String PAGES_PATH = "pages";

    public static final String DEFAULT_PAGE = "/home.html";

    public static final String OK_STATUS = "OK";
    public static final String FOUND_STATUS = "Found";
    public static final String NOT_MODIFIED_STATUS = "Not Modified";
    public static final String BAD_REQUEST_STATUS = "Bad Request";
    public static final String UNAUTHORIZED_STATUS = "Unathorized";
    public static final String NOT_FOUND_STATUS = "Not Found";
    public static final String INTERNAL_SERVER_ERROR_STATUS = "Internal Server Error";

    public static final Integer OK_STATUS_CODE = 200;
    public static final Integer FOUND_STATUS_CODE = 302;
    public static final Integer NOT_MODIFIED_STATUS_CODE = 304;
    public static final Integer BAD_REQUEST_STATUS_CODE = 400;
    public static final Integer UNAUTHORIZED_STATUS_CODE = 401;
    public static final Integer NOT_FOUND_CODE = 404;
    public static final Integer INTERNAL_SERVER_ERROR_STATUS_CODE = 500;

    public WebConstraints() { }
}