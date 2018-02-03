package javache;

import java.io.IOException;

public class StartUp {
    public static void main(String[] args) {
        int port = WebConstraints.DEFAULT_PORT;

        if(args.length > 1){
            port = Integer.parseInt(args[1]);
        }

        Server server = new Server(port);

        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
