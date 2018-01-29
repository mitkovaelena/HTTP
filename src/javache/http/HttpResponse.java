package javache.http;

import javache.WebConstraints;

import java.util.Arrays;
import java.util.HashMap;

public interface HttpResponse {
    enum ResponseLines {
        OK (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.Ok.getStatusPhrase()),
        CREATED (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.Created.getStatusPhrase()),
        NO_CONTENT (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.NoContent.getStatusPhrase()),
        SEE_OTHER (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.SeeOther.getStatusPhrase()),
        BAD_REQUEST (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.BadRequest.getStatusPhrase()),
        UNAUTHORIZED (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.Unauthorized.getStatusPhrase()),
        FORBIDDEN (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.Forbidden.getStatusPhrase()),
        NOT_FOUND (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.NotFound.getStatusPhrase()),
        INTERNAL_SERVER_ERROR (WebConstraints.SERVER_HTTP_VERSION + " " + HttpStatus.InternalServerError.getStatusPhrase());

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
