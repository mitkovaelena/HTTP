package application;

public class ApplicationConstants {
    public final static String RESOURCES_PATH =  System.getProperty("user.dir") +"/src/main/java/application/resources/";
    public final static String ASSETS_PATH = ApplicationConstants.RESOURCES_PATH + "assets/";
    public final static String USERS_DB_PATH = ApplicationConstants.RESOURCES_PATH + "db/users.txt";
    public final static String PAGES_PATH =  ApplicationConstants.RESOURCES_PATH + "templates/";

    public static final String INDEX_PAGE = "/html/index.html";
    public static final String LOGIN_PAGE = "/html/login.html";

    public static final String INDEX_ROUTE = "/";
    public static final String REGISTER_ROUTE = "/users/register";
    public static final String LOGIN_ROUTE = "/users/login";
    public static final String LOGOUT_ROUTE = "/users/logout";
    public static final String PROFILE_ROUTE = "/users/profile";

    public static final String SERVER_NAME_AND_VERSION = "Javache/-1.0.0";
    public static final String SERVER_HEADER = "Server: ";
    public static final String DATE_HEADER = "Date: ";
    public static final String CONTENT_TYPE_HEADER = "Content-Type: ";
    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition: ";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length: ";
    public static final String LOCATION_HEADER = "Location: ";
    public static final String CONTENT_DISPOSITION_VALUE_INLINE = "inline";

    public ApplicationConstants() { }
}