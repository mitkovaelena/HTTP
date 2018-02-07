package application;

import database.repositories.Repository;
import database.repositories.UserRepository;
import javache.Application;
import javache.WebConstraints;
import javache.http.*;
import javache.io.Reader;
import database.models.User;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class CasebookApplication implements Application {
    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("Casebook");

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private HttpSession httpSession;
    private Repository repository;
    private HashMap<String, String> supportedContentTypes;

    public CasebookApplication() {
        this.supportedContentTypes = new HashMap<>();
        this.repository = new UserRepository(ENTITY_MANAGER_FACTORY);
        this.seedSupportedContentTypes();
    }

    public byte[] handleRequest(HttpContext httpContext) {
        this.httpRequest = httpContext.getHttpRequest();
        this.httpResponse = httpContext.getHttpResponse();

        byte[] resourceData = new byte[0];

        String url = this.httpRequest.getRequestUrl();
        switch (url) {
            case AppConstraints.INDEX_ROUTE:
                resourceData = this.getResource(AppConstraints.INDEX_PAGE);
                break;

            case AppConstraints.REGISTER_ROUTE:
                String registerEmail = this.httpRequest.getBodyParameters().get("email");
                String registerPass = this.httpRequest.getBodyParameters().get("password");
                String confirmPass = this.httpRequest.getBodyParameters().get("password_confirm");
                if (!registerPass.equals(confirmPass)) {
                    this.httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
                    resourceData = "<h1>Passwords mismatch</h1>".getBytes();
                } else {
                        User existingUser = (User) repository.doAction("findByEmail",registerEmail);
                        if (existingUser != null) {
                            this.httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
                            resourceData = "<h1>User already exists</h1>".getBytes();
                        } else {
                            repository.doAction("create", registerEmail, registerPass);
                            this.httpResponse.setStatusCode(HttpStatus.SEE_OTHER);
                            this.httpResponse.addHeader(WebConstraints.LOCATION_HEADER, AppConstraints.LOGIN_PAGE);
                        }
                }
                break;

            case AppConstraints.LOGIN_ROUTE:
                String loginEmail = this.httpRequest.getBodyParameters().get("email");
                String loginPass = this.httpRequest.getBodyParameters().get("password");
                    User loggedUser = (User) repository.doAction("findByEmail",loginEmail);
                    if (loggedUser == null) {
                        this.httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
                        resourceData = "<h1>User doesn't exist</h1>".getBytes();
                    } else if (!loggedUser.getPassword().equals(loginPass)) {
                        this.httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
                        resourceData = "<h1>Wrong Username/Password</h1>".getBytes();
                    } else {
                        String sessionId = UUID.randomUUID().toString();

                        this.httpSession.setSessionData(
                                sessionId, new HashMap<String, Object>(){{
                                    put("userId", loggedUser.getId());
                                }}
                        );

                        this.httpResponse.setStatusCode(HttpStatus.SEE_OTHER);
                        this.httpResponse.addCookie("sessionId", sessionId);
                        this.httpResponse.addHeader(WebConstraints.LOCATION_HEADER, AppConstraints.PROFILE_ROUTE);
                    }
                break;

            case AppConstraints.PROFILE_ROUTE:
                try {
                    String sessionId = this.httpRequest.getCookies().get("sessionId");
                    if (sessionId == null) {
                        resourceData = Reader.readAllBytes(new FileInputStream(AppConstraints.PAGES_PATH + "/profile/guest.html"));
                        this.httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                    } else {
                        String loggedUserId = (String) this.httpSession.getSessionData(sessionId).get("userId");
                        User foundUser = (User) repository.doAction("findById",loggedUserId);;
                        if (foundUser == null) {
                            this.httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                        } else {
                            String loggedContent = Reader.readAllLines(
                                    new FileInputStream(
                                            AppConstraints.PAGES_PATH + "/profile/logged.html"));
                            resourceData = String.format(loggedContent,
                                    foundUser.getEmail(),
                                    foundUser.getPassword()).getBytes();
                            this.httpResponse.setStatusCode(HttpStatus.OK);
                        }
                    }
                } catch (IOException | NullPointerException e) {
                    this.httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    resourceData = "<h1>Something went wrong</h1>".getBytes();
                    e.printStackTrace();
                }
                break;

            case AppConstraints.LOGOUT_ROUTE:
                this.httpResponse.addCookie("sessionId", this.httpRequest.getCookies().get("sessionId") + "; max-Age = -1");
                this.httpResponse.setStatusCode(HttpStatus.SEE_OTHER);
                this.httpResponse.addHeader(WebConstraints.LOCATION_HEADER, AppConstraints.INDEX_PAGE);

                break;

            default:
                resourceData = this.getResource(url);
        }

        this.httpResponse.setContent(resourceData);
        this.setResponseHeaders();
        return this.httpResponse.getBytes();
    }

    @Override
    public void setSession(HttpSession session) {
        this.httpSession = session;
    }

    private byte[] getResource(String url) {
        String pathName = AppConstraints.ASSETS_PATH + url;
        File file = new File(pathName);

        byte[] fileByteData = null;

        if (!file.exists() || file.isDirectory()) {
            this.httpResponse.setStatusCode(HttpStatus.NOT_FOUND);
        } else {
            try {
                if (!file.getCanonicalPath().startsWith(AppConstraints.ASSETS_PATH)) {
                    this.httpResponse.setStatusCode(HttpStatus.BAD_REQUEST);
                }

                fileByteData = Reader.readAllBytes(new FileInputStream(pathName));
                this.httpResponse.setStatusCode(HttpStatus.OK);

            } catch (AccessDeniedException e) {
                this.httpResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
                e.printStackTrace();
            } catch (IOException e) {
                this.httpResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
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
