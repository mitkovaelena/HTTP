package javache;

import java.io.File;
import java.io.IOException;

public class StartUp {

    public static void main(String[] args) {
        int port = WebConstants.DEFAULT_PORT;

        if(args.length > 0){
            port = Integer.parseInt(args[0]);
        }

        Iterable<RequestHandler> handlers = RequestHandlerLoader
                .scanRequestHandlers(WebConstants.HANDLERS_FOLDER);

        Server server = new Server(port, handlers);

        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
