package com.github.walkgs.cojt.cojys.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

public interface CJY2Loader {

    Class<?> loadClass(String className) throws ClassNotFoundException;

    Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException;

    Class<?> findClass(String name) throws ClassNotFoundException;

    Class<?> findClass(String name, boolean checkAll) throws ClassNotFoundException;

    Enumeration<URL> getResources(String name) throws IOException;

    InputStream getResourceAsStream(String name) throws IOException;

    URL getResource(String name) throws IOException;

    ClassLoader getParent();

}
