package javache;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class RequestHandlerLoader {
    public static Iterable<RequestHandler> scanRequestHandlers(String libDirectoryPath) {
        Set<RequestHandler> requestHandlers = new HashSet<>();
        File libDirectory = new File(libDirectoryPath);

        if (libDirectory.exists() && libDirectory.isDirectory()) {
            for (File file : libDirectory.listFiles()) {
                if (file.isDirectory()) {
                    scanRequestHandlers(file.getPath());
                } else if (file.getName().endsWith(".class")) {
                    requestHandlers.add(loadRequestHandlers(file));
                }
            }
        }

        return requestHandlers;
    }

    private static RequestHandler loadRequestHandlers(File file) {
        String className = file.getPath().substring(0, file.getPath().lastIndexOf('.'))
                .replace(WebConstants.HANDLERS_FOLDER + File.separator, "")
                .replace(File.separatorChar, '.');

        try {
            URL[] urls = {new File(WebConstants.HANDLERS_FOLDER).toURI().toURL()};
            ClassLoader cl = new URLClassLoader(urls);
            Class<?> handlerClass =  cl.loadClass(className);
            if (RequestHandler.class.isAssignableFrom(handlerClass)) {
                return (RequestHandler) handlerClass.getConstructor(String.class)
                        .newInstance(WebConstants.ROOT_PATH);
            }

        } catch (MalformedURLException | ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return null;
    }
}