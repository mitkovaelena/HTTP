package application;

public class AppConstraints {
    public final static String RESOURCES_PATH =  System.getProperty("user.dir") +"/src/resources/";
    public final static String ASSETS_PATH = AppConstraints.RESOURCES_PATH + "assets/";
    public final static String USERS_DB_PATH = AppConstraints.RESOURCES_PATH + "db/users.txt";
    public final static String PAGES_PATH =  AppConstraints.RESOURCES_PATH + "pages/";

    public static final String INDEX_PAGE = "/html/index.html";
    public static final String LOGIN_PAGE = "/html/login.html";

    public static final String INDEX_ROUTE = "/";
    public static final String REGISTER_ROUTE = "/users/register";
    public static final String LOGIN_ROUTE = "/users/login";
    public static final String LOGOUT_ROUTE = "/users/logout";
    public static final String PROFILE_ROUTE = "/users/profile";

    public AppConstraints() { }
}