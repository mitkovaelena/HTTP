package javache;

import java.io.IOException;

public class StartUp {
    public static void main(String[] args) {
        Server server = new Server(8000);

        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
