package javache;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class RequestHandlerLoader {
    private Map<String, RequestHandler> requestHandlers;

    public RequestHandlerLoader(String libDirectoryPath) {
        this.requestHandlers = new HashMap<>();
        this.scanLibraries(libDirectoryPath);
    }

    public Map<String, RequestHandler> getRequestHandlers() {
        return Collections.unmodifiableMap(requestHandlers);
    }

    public void scanLibraries(String libDirectoryPath) {
        try {
            File libDirectory = new File(libDirectoryPath);

            if (libDirectory.exists() && libDirectory.isDirectory()) {
                for (File file : libDirectory.listFiles()) {
                    if (!file.getName().endsWith(".jar")) continue;

                    JarFile library = new JarFile(file.getCanonicalPath());
                    this.loadLibraries(library, file.getCanonicalPath());
                }
            }
        }catch (IOException | ReflectiveOperationException e){
            e.printStackTrace();
        }
    }

    private void loadLibraries(JarFile library, String path) throws MalformedURLException, ReflectiveOperationException {
        Enumeration<JarEntry> jarEntries = library.entries();
        URL[] urls = {new URL("jar:file:" + path + "!/")};
        ClassLoader cl = new URLClassLoader(urls);

        while (jarEntries.hasMoreElements()) {
            JarEntry currentFile = jarEntries.nextElement();

            if (currentFile.isDirectory() || !currentFile.getName().endsWith(".class")) continue;

            String className = currentFile.getName()
                    .replace(".class", "")
                    .replace("/", ".");
            Class<?> handlerClass = cl.loadClass(className);
            if (RequestHandler.class.isAssignableFrom(handlerClass)) {
                RequestHandler requestHandler =  (RequestHandler) handlerClass.getDeclaredConstructor(String.class)
                        .newInstance(WebConstants.ROOT_PATH);
                this.requestHandlers.putIfAbsent(requestHandler.getClass().getSimpleName(), requestHandler);
            }

        }

    }
}