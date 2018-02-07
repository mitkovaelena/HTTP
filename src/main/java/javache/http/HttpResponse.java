package javache.http;

import javache.WebConstraints;

import java.util.Arrays;
import java.util.HashMap;

public interface HttpResponse {
    enum ResponseLines {
        OK (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.OK.getStatusPhrase()),
        CREATED (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.CREATED.getStatusPhrase()),
        NO_CONTENT (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.NO_CONTENT.getStatusPhrase()),
        SEE_OTHER (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.SEE_OTHER.getStatusPhrase()),
        BAD_REQUEST (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.BAD_REQUEST.getStatusPhrase()),
        UNAUTHORIZED (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.UNAUTHORIZED.getStatusPhrase()),
        FORBIDDEN (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.FORBIDDEN.getStatusPhrase()),
        NOT_FOUND (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.NOT_FOUND.getStatusPhrase()),
        INTERNAL_SERVER_ERROR (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.INTERNAL_SERVER_ERROR.getStatusPhrase());

        private String value;

        ResponseLines(String responseLine) {
            this.value = responseLine;
        }

        static String getResponseLine(int statusCode) {
            return ((ResponseLines) Arrays.stream(values()).filter((x) -> x.value.contains("" + statusCode)).toArray()[0]).value;
        }
    }

    HashMap<String, String> getHeaders();

    HttpStatus getStatusCode();

    byte[] getContent();

    byte[] getBytes();

    void setStatusCode(HttpStatus statusCode);

    void setContent(byte[] content);

    void addHeader(String header, String value);

    void addCookie(String cookie, String value);
}
