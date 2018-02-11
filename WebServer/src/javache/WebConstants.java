package javache;

import java.io.File;

public class WebConstants {
    public static final String SERVER_HTTP_VERSION = "HTTP/1.1";
    public static final int DEFAULT_PORT = 8000;
    public static final int SOCKET_TIMEOUT_MILLISECONDS = 5000;

    static final String ROOT_PATH = new File(StartUp.class.getProtectionDomain().getCodeSource()
            .getLocation().getPath()).toString();
    static final String HANDLERS_FOLDER = ROOT_PATH + File.separator + "lib";

    public static final String SET_COOKIE_HEADER = "Set-Cookie: ";

    public WebConstants() { }

}