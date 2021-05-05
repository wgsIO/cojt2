package com.github.walkgs.cojt.cojys.loader.impl;

import com.github.walkgs.cojt.cojys.loader.CJY2Finder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@RequiredArgsConstructor
public class CJY2FinderImpl implements CJY2Finder {

    private static final FileFilter CLASS_FILTER = jar -> jar.getName().toLowerCase().endsWith(".class");

    private final ClassLoader loader;
    private ClassLoader oldLoader;

    public CJY2FinderImpl() {
        loader = new CJY2LoaderImpl();
    }

    @Override
    public Class<?> findClass(@NonNull final String name) throws ClassNotFoundException {
        checkState();
        return Class.forName(name);
    }

    @Override
    public Map<String, Set<Class<?>>> findClasses(@NonNull final String... packagesName) throws IOException, ClassNotFoundException {
        checkState();
        final Map<String, Set<Class<?>>> classes = new HashMap<>();
        for (String packageName : packagesName)
            classes.putAll(findClasses(packageName, true));
        return classes;
    }

    @Override
    public Map<String, Set<Class<?>>> findClasses(@NonNull final Package... packages) throws IOException, ClassNotFoundException {
        checkState();
        final Map<String, Set<Class<?>>> classes = new HashMap<>();
        for (Package packag : packages)
            classes.putAll(findClasses(packag.getName(), true));
        return classes;
    }

    @Override
    public Map<String, Set<Class<?>>> findClasses(@NonNull final String packageName, final boolean convert) throws IOException, ClassNotFoundException {
        checkState();
        final String path = convert ? packageName.replace(".", "/") : packageName;
        final Enumeration<URL> resources = loader.getResources(path);
        final Set<File> directories = new HashSet<>();
        while (resources.hasMoreElements())
            directories.add(new File(resources.nextElement().getFile()));
        final Map<String, Set<Class<?>>> classes = new HashMap<>();
        for (File directory : directories)
            classes.putAll(findClasses(directory, packageName));
        return classes;
    }

    @Override
    public Map<String, Set<Class<?>>> findClasses(@NonNull final File directory, @NonNull final String packageName) throws ClassNotFoundException {
        checkState();
        final Map<String, Set<Class<?>>> classes = new HashMap<>();
        if (!directory.exists())
            return classes;
        final File[] files = directory.listFiles();
        if (files == null)
            return classes;
        ClassNotFoundException exception = null;
        for (File file : files) {
            try {
                if (!file.isDirectory()) {
                    if (!CLASS_FILTER.accept(file))
                        continue;
                    final Class<?> clazz = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                    if (clazz == null)
                        continue;
                    final Set<Class<?>> classSet = classes.computeIfAbsent(clazz.getPackage().getName(), it -> new HashSet<>());
                    if (classSet.contains(clazz))
                        continue;
                    classSet.add(clazz);
                    continue;
                }
                assert !file.getName().contains(".");
                classes.putAll(findClasses(file, packageName + "." + file.getName()));
            } catch (ClassNotFoundException e) {
                if (exception == null) {
                    exception = e;
                    continue;
                }
                exception.addSuppressed(e);
            }
        }
        if (exception != null)
            throw exception;
        return classes;
    }

    @Override
    public CJY2Finder open() {
        if (oldLoader != null)
            throw new IllegalStateException();
        final Thread thread = Thread.currentThread();
        oldLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(loader);
        return this;
    }

    @Override
    public boolean isOpened() {
        return oldLoader != null;
    }

    @Override
    public void close() {
        if (oldLoader == null)
            throw new IllegalStateException();
        final Thread thread = Thread.currentThread();
        thread.setContextClassLoader(oldLoader);
        oldLoader = null;
    }

    private void checkState() {
        if (!isOpened())
            throw new IllegalStateException();
    }

}
