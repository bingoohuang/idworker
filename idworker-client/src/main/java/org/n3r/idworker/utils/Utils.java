package org.n3r.idworker.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {

    public static ClassLoader getClassLoader() {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        return contextClassLoader != null ? contextClassLoader : Utils.class.getClassLoader();
    }


    public static InputStream classResourceToStream(String resourceName) {
        return getClassLoader().getResourceAsStream(resourceName);
    }


    public static String firstLine(String classResourceName) {
        InputStream inputStream = null;
        try {
            inputStream = classResourceToStream(classResourceName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            return bufferedReader.readLine();
        } catch (IOException e) {
            return null;
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static String checkNotEmpty(String param, String name) {
        if (param == null || param.isEmpty()) throw new IllegalArgumentException(name + " is empty");

        return param;
    }
}
