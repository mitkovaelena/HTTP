package javache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ServerConfig {

    private static final String REQUEST_HANDLERS_LABEL = "request-handlers: ";
    private static final String CONFIG_FILE_NAME = "config.ini";
    private static final String CONFIG_FULL_PATH = WebConstants.ROOT_PATH + File.separator + CONFIG_FILE_NAME;


    private Set<String> handlers;

    public ServerConfig() {
        this.handlers = new LinkedHashSet<>();
    }

    public void initializeConfig() {
        this.handlers = new LinkedHashSet<>();
        try {
            List<String> configFileContents = Files.readAllLines(Paths.get(CONFIG_FULL_PATH));
            for (String configLine : configFileContents) {
                if (configLine.startsWith(REQUEST_HANDLERS_LABEL)) {
                    this.loadRequestHandlersConfig(configLine);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRequestHandlersConfig(String configLine) {
        String[] tokens = configLine.replace(REQUEST_HANDLERS_LABEL, "").trim().split(",\\s+");
        this.handlers.addAll(Arrays.asList(tokens));
    }

    public Set<String> getHandlers() {
        return Collections.unmodifiableSet(handlers);
    }

}