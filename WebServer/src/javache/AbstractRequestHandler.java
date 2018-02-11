package javache;

public abstract class AbstractRequestHandler implements RequestHandler {
    protected final String serverRootPath;

    public AbstractRequestHandler(String serverRootPath) {
        this.serverRootPath = serverRootPath;
    }
}
