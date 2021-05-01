package com.github.walkgs.cojt.cojys.loader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;

public interface CJY2Finder extends Closeable {

    static URLClassLoader resolve(File[] files) {
        return resolve(files, null);
    }

    static URLClassLoader resolve(File[] files, ClassLoader parent) {
        try {
            final URL[] urls = new URL[files.length];
            for (int i = 0; i < files.length; i++)
                urls[i] = files[i].toURI().toURL();
            return parent != null ? URLClassLoader.newInstance(urls, parent) : URLClassLoader.newInstance(urls);
        } catch (MalformedURLException $) {
            return null;
        }
    }

    Class<?> findClass(String name) throws ClassNotFoundException;

    Map<String, Set<Class<?>>> findClasses(String... packagesName) throws IOException, ClassNotFoundException;

    Map<String, Set<Class<?>>> findClasses(Package... packages) throws IOException, ClassNotFoundException;

    Map<String, Set<Class<?>>> findClasses(String packageName, boolean convert) throws IOException, ClassNotFoundException;

    Map<String, Set<Class<?>>> findClasses(File directory, String packageName) throws IOException, ClassNotFoundException;

    CJY2Finder open();

    void close();

    boolean isOpened();

}
