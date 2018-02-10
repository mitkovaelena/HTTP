import application.CasebookApplication;
import javache.Application;
import javache.Server;
import javache.WebConstants;

import java.io.IOException;

public class StartUp {
    public static void main(String[] args) {
        int port = WebConstants.DEFAULT_PORT;

        if(args.length > 1){
            port = Integer.parseInt(args[1]);
        }

        Application application = new CasebookApplication();
        Server server = new Server(port, application);

        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
