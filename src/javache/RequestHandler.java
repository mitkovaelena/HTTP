package javache;

import javache.http.*;
import javache.io.Reader;
import javache.models.User;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class RequestHandler {
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private HttpSession httpSession;
    private HashMap<String, String> supportedContentTypes;

    public RequestHandler(HttpSession httpSession) {
        this.supportedContentTypes = new HashMap<>();
        this.seedSupportedContentTypes();
        this.httpSession = httpSession;
    }

    public byte[] handleRequest(String requestContent) {
        this.httpRequest = new HttpRequestImpl(requestContent);
        this.httpResponse = new HttpResponseImpl();

        byte[] resourceData = new byte[0];

        String url = this.httpRequest.getRequestUrl();
        switch (url) {
            case WebConstraints.INDEX_ROUTE:
                resourceData = this.getResource(WebConstraints.INDEX_PAGE);
                break;

            case WebConstraints.REGISTER_ROUTE:
                String registerEmail = this.httpRequest.getBodyParameters().get("email");
                String registerPass = this.httpRequest.getBodyParameters().get("password");
                String confirmPass = this.httpRequest.getBodyParameters().get("password_confirm");
                if (!registerPass.equals(confirmPass)) {
                    this.httpResponse.setStatusCode(HttpStatus.BadRequest);
                    resourceData = "<h1>Passwords mismatch</h1>".getBytes();
                } else {
                    try {
                        User existingUser = this.findUserByEmail(registerEmail);
                        if (existingUser != null) {
                            this.httpResponse.setStatusCode(HttpStatus.BadRequest);
                            resourceData = "<h1>User already exists</h1>".getBytes();
                        } else {
                            this.writeUserData(new User(registerEmail, registerPass));
                            this.httpResponse.setStatusCode(HttpStatus.SeeOther);
                            this.httpResponse.addHeader(WebConstraints.LOCATION_HEADER, WebConstraints.LOGIN_PAGE);
                        }
                    } catch (IOException e) {
                        this.httpResponse.setStatusCode(HttpStatus.InternalServerError);
                        resourceData = "<h1>Something went wrong</h1>".getBytes();
                        e.printStackTrace();
                    }
                }
                break;

            case WebConstraints.LOGIN_ROUTE:
                String loginEmail = this.httpRequest.getBodyParameters().get("email");
                String loginPass = this.httpRequest.getBodyParameters().get("password");
                try {
                    User user = this.findUserByEmail(loginEmail);
                    if (user == null) {
                        this.httpResponse.setStatusCode(HttpStatus.BadRequest);
                        resourceData = "<h1>User doesn't exist</h1>".getBytes();
                    } else if (!user.getPassword().equals(loginPass)) {
                        this.httpResponse.setStatusCode(HttpStatus.BadRequest);
                        resourceData = "<h1>Wrong Username/Password</h1>".getBytes();
                    } else {
                        String sessionId = UUID.randomUUID().toString();

                        this.httpSession.setSessionData(
                                sessionId, new HashMap<String, Object>(){{
                                    put("userId", user.getId());
                                }}
                        );

                        this.httpResponse.setStatusCode(HttpStatus.SeeOther);
                        this.httpResponse.addCookie("sessionId", sessionId);
                        this.httpResponse.addHeader(WebConstraints.LOCATION_HEADER, WebConstraints.PROFILE_ROUTE);
                    }
                } catch (IOException e) {
                    this.httpResponse.setStatusCode(HttpStatus.InternalServerError);
                    resourceData = "<h1>Something went wrong</h1>".getBytes();
                    e.printStackTrace();
                }
                break;

            case WebConstraints.PROFILE_ROUTE:
                try {
                    String sessionId = this.httpRequest.getCookies().get("sessionId");
                    if (sessionId == null) {
                        resourceData = Reader.readAllBytes(new FileInputStream(WebConstraints.PAGES_PATH + "/profile/guest.html"));
                        this.httpResponse.setStatusCode(HttpStatus.Unauthorized);
                    } else {
                        String loggedUserId = (String) this.httpSession.getSessionData(sessionId).get("userId");
                        User user = findUserById(loggedUserId);
                        if (user == null) {
                            this.httpResponse.setStatusCode(HttpStatus.Unauthorized);
                        } else {
                            String loggedContent = Reader.readAllLines(
                                    new FileInputStream(
                                            WebConstraints.PAGES_PATH + "/profile/logged.html"));
                            resourceData = String.format(loggedContent,
                                    user.getEmail(),
                                    user.getPassword()).getBytes();
                            this.httpResponse.setStatusCode(HttpStatus.Ok);
                        }
                    }
                } catch (IOException | NullPointerException e) {
                    this.httpResponse.setStatusCode(HttpStatus.InternalServerError);
                    resourceData = "<h1>Something went wrong</h1>".getBytes();
                    e.printStackTrace();
                }
                break;

            case WebConstraints.LOGOUT_ROUTE:
                this.httpResponse.addCookie("sessionId", this.httpRequest.getCookies().get("sessionId") + "; max-Age = -1");
                this.httpResponse.setStatusCode(HttpStatus.SeeOther);
                this.httpResponse.addHeader(WebConstraints.LOCATION_HEADER, WebConstraints.INDEX_PAGE);

                break;

            default:
                resourceData = this.getResource(url);
        }


        this.httpResponse.setContent(resourceData);
        this.setResponseHeaders();
        return this.httpResponse.getBytes();
    }

    private void writeUserData(User user) throws IOException {
        try (FileWriter fileWriter = new FileWriter(WebConstraints.USERS_DB_PATH, true)) {
            fileWriter.append(UUID.randomUUID().toString()).append("|")
                    .append(user.getEmail()).append("|")
                    .append(String.valueOf(user.getPassword()))
                    .append(System.lineSeparator());
            fileWriter.flush();
        }
    }

    private User findUserByEmail(String email) throws IOException {
        return this.findUserData(email, 1);
    }

    private User findUserById(String id) throws IOException {
        return this.findUserData(id, 0);
    }

    private User findUserData(String searchStr, int index) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(WebConstraints.USERS_DB_PATH))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] userStr = line.split("\\|");
                if (userStr[index].equals(searchStr)) {
                    return new User(userStr[0], userStr[1], userStr[2]);
                }
            }
        }
        return null;
    }

    private byte[] getResource(String url) {
        String pathName = WebConstraints.ASSETS_PATH + url;
        File file = new File(pathName);

        byte[] fileByteData = null;

        if (!file.exists() || file.isDirectory()) {
            this.httpResponse.setStatusCode(HttpStatus.NotFound);
        } else {
            try {
                if (!file.getCanonicalPath().startsWith(WebConstraints.ASSETS_PATH)) {
                    this.httpResponse.setStatusCode(HttpStatus.BadRequest);
                }

                fileByteData = Reader.readAllBytes(new FileInputStream(pathName));
                this.httpResponse.setStatusCode(HttpStatus.Ok);

            } catch (AccessDeniedException e) {
                this.httpResponse.setStatusCode(HttpStatus.Unauthorized);
                e.printStackTrace();
            } catch (IOException e) {
                this.httpResponse.setStatusCode(HttpStatus.InternalServerError);
                e.printStackTrace();
            }
        }
        return fileByteData;
    }

    private void setResponseHeaders() {
        this.httpResponse.addHeader(WebConstraints.SERVER_HEADER, WebConstraints.SERVER_NAME_AND_VERSION);
        this.httpResponse.addHeader(WebConstraints.DATE_HEADER, new Date().toString());

        if (this.verifyResourceStatus()) {
            this.httpResponse.addHeader(WebConstraints.CONTENT_TYPE_HEADER, this.getContentType(this.httpRequest.getRequestUrl()));
            this.httpResponse.addHeader(WebConstraints.CONTENT_DISPOSITION_HEADER, WebConstraints.CONTENT_DISPOSITION_VALUE_INLINE);
            this.httpResponse.addHeader(WebConstraints.CONTENT_LENGTH_HEADER, String.valueOf(this.httpResponse.getContent().length));
        }
    }

    private void seedSupportedContentTypes() {
        this.supportedContentTypes.put("png", "image/png");
        this.supportedContentTypes.put("jpg", "image/jpeg");
        this.supportedContentTypes.put("jpeg", "image/jpeg");
        this.supportedContentTypes.put("css", "text/css");
        this.supportedContentTypes.put("html", "text/html");
    }

    private String getContentType(String resourceUrl) {
        String resourceExtension = resourceUrl.substring(resourceUrl.lastIndexOf(".") + 1);
        if (this.supportedContentTypes.containsKey(resourceExtension)) {
            return this.supportedContentTypes.get(resourceExtension);
        }

        return "text/html";
    }

    private boolean verifyResourceStatus() {
        return this.httpResponse.getStatusCode().getStatusCode() == 200;
    }

}
