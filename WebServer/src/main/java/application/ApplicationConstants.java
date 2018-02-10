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

    public ApplicationConstants() { }
}