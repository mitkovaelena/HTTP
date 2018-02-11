package javache.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Reader {
    private Reader() {
    }

    public static String readAllLines(InputStream csInputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(csInputStream));
        StringBuilder output = new StringBuilder();

        while (reader.ready()){
            output.append((char)reader.read());
        }

        return output.toString();
    }
}
