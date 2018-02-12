package solet;

import javache.http.HttpStatus;

import java.util.Date;

public abstract class BaseHttpSolet implements HttpSolet {
    private static final String SERVER_NAME_AND_VERSION = "Javache/-1.0.0";
    private static final String SERVER_HEADER = "Server: ";
    private static final String DATE_HEADER = "Date: ";
    private static final String CONTENT_TYPE_HEADER = "Content-Type: ";
    private static final String CONTENT_TYPE_HTML = "text/html";
    private static final String NOT_FOUND_MESSAGE = "<h1 style='background-color:red'>Error: %s Action Not Found</h1>";

    @Override
    public void doGet(HttpSoletRequest request, HttpSoletResponse response) {
      this.notFound(response, "GET ");
    }

    @Override
    public void doPost(HttpSoletRequest request, HttpSoletResponse response) {
        this.notFound(response, "POST");
    }

    @Override
    public void doPut(HttpSoletRequest request, HttpSoletResponse response) {
        this.notFound(response, "PUT");
    }

    @Override
    public void doDelete(HttpSoletRequest request, HttpSoletResponse response) {
        this.notFound(response, "DELETE");
    }

    private void notFound(HttpSoletResponse response, String action){
        response.setStatusCode(HttpStatus.NOT_FOUND);

        response.addHeader(SERVER_HEADER, SERVER_NAME_AND_VERSION);
        response.addHeader(DATE_HEADER, new Date().toString());
        response.addHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_HTML);

        response.setContent(String.format(NOT_FOUND_MESSAGE, action).getBytes());
    }
}
