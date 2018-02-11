package javache;

import java.io.IOException;
import java.util.Map;

public class StartUp {

    public static void main(String[] args) {
        int port = WebConstants.DEFAULT_PORT;

        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }

        Server server = new Server(port, new RequestHandlerLoader(WebConstants.HANDLERS_FOLDER));

        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
