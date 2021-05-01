package com.github.walkgs.cojt.codit.resolver;

import com.github.walkgs.cojt.codit.storage.Cache;
import com.github.walkgs.cojt.codit.storage.CacheMap;
import com.github.walkgs.cojt.codit.storage.SimpleCacheMap;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class BasicClassResolver implements ClassResolver {

    private static final Set<String> EMPTY_SET = new HashSet<>();

    private static final String CACHE_PACKAGES_NAME = "packages";
    private static final String CACHE_PROPERTIES_NAME = "properties";
    private static final String CACHE_CLASSES_NAME = "classes";

    private transient final CacheMap cacheMap = new SimpleCacheMap().apply($cache -> {
        $cache.create(CACHE_PACKAGES_NAME);
        $cache.create(CACHE_PROPERTIES_NAME);
        $cache.create(CACHE_CLASSES_NAME);
    });

    public BasicClassResolver(Set<String> packagesName) {
        this(packagesName, EMPTY_SET);
    }

    public BasicClassResolver(Set<String> packagesName, Set<String> ignore) {
        final Cache<String> packages = cacheMap.find(CACHE_PACKAGES_NAME);
        for (String packageName : packagesName)
            packages.add(packageName);
        packages.add(getClass().getPackage().getName());
        final Set<URL> urls = new HashSet<>();
        for (String packageName : packages)
            addUrlsForPackage(urls, packageName);
        for (URL url : urls) {
            try {
                final String path = URLDecoder.decode(url.getPath(), "UTF-8");
                if (!path.endsWith(".jar")) {
                    final File dir = new File(path);
                    recurseSubDir(dir, dir.getAbsolutePath().length() + 1, ignore);
                    continue;
                }
                try (JarInputStream stream = new JarInputStream(url.openStream())) {
                    for (JarEntry entry = stream.getNextJarEntry(); entry != null; entry = stream.getNextJarEntry())
                        checkAndAdd(entry.getName(), ignore);
                }

            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public <T> Set<Class<T>> getSubClassesOf(Class<T> clazz) {
        return getSubClassesOf(clazz, EMPTY_SET);
    }

    @Override
    public <T> Set<Class<T>> getSubClassesAnnotatedOf(Class<T> clazz, Class<? extends Annotation> annotation) {
        return getSubClassesAnnotatedOf(clazz, EMPTY_SET, annotation);
    }

    @Override
    public <T> Set<Class<T>> getClassesAnnotated(Class<? extends Annotation> annotation) {
        return getClassesAnnotated(EMPTY_SET, annotation);
    }

    @Override
    public <T> Set<Class<T>> getClassesAnnotatedItems(Class<? extends Annotation> annotation) {
        return getClassesAnnotatedItems(EMPTY_SET, annotation);
    }

    @Override
    public <T> Set<Class<T>> getSubClassesOf(Class<T> clazz, Set<String> ignore) {
        final Set<Class<T>> result = new HashSet<>();
        final Collection<Class<T>> classes = getClasses();
        for (Class<T> root : classes) {
            if (ignore.contains(root.getSimpleName()) || (root.isInterface() && !clazz.isAssignableFrom(root) && ((root.getModifiers() & Modifier.ABSTRACT) != 0)))
                continue;
            result.add(root);
        }
        return result;
    }

    @Override
    public <T> Set<Class<T>> getSubClassesAnnotatedOf(Class<T> clazz, Set<String> ignore, Class<? extends Annotation> annotation) {
        final Set<Class<T>> result = new HashSet<>();
        final Collection<Class<T>> classes = getClasses();
        for (Class<T> root : classes) {
            if (ignore.contains(root.getSimpleName()) || (root.getAnnotation(annotation) == null && !clazz.isAssignableFrom(root) && root.isInterface()))
                continue;
            result.add(root);
        }
        return result;
    }

    @Override
    public <T> Set<Class<T>> getClassesAnnotated(Set<String> ignore, Class<? extends Annotation> annotation) {
        final Set<Class<T>> result = new HashSet<>();
        final Collection<Class<T>> classes = getClasses();
        for (Class<T> clazz : classes) {
            if (ignore.contains(clazz.getSimpleName()) || (clazz.isInterface() && clazz.getAnnotation(annotation) == null && ((clazz.getModifiers() & Modifier.ABSTRACT) != 0)))
                continue;
            result.add(clazz);
        }
        return result;
    }

    @Override
    public <T> Set<Class<T>> getClassesAnnotatedItems(Set<String> ignore, Class<? extends Annotation> annotation) {
        final Set<Class<T>> result = new HashSet<>();
        final Collection<Class<T>> classes = getClasses();
        for (Class<T> clazz : classes) {
            if (ignore.contains(clazz.getSimpleName()) || clazz.getAnnotation(annotation) == null)
                continue;
            result.add(clazz);
        }
        return result;
    }

    @Override
    public SortedSet<String> getProperties(String path) {
        final SortedSet<String> result = new TreeSet<>();
        for (String property : cacheMap.<String>find(CACHE_PROPERTIES_NAME)) {
            if (!property.startsWith(path))
                continue;
            result.add(property);
        }
        return result;
    }

    private <T> Collection<Class<T>> getClasses() {
        final Set<Class<T>> result = new HashSet<>();
        for (String className : cacheMap.<String>find(CACHE_CLASSES_NAME))
            try {
                final Class<T> clazz = loadClass(className);
                result.add(clazz);
            } catch (ClassNotFoundException | Error ignored) {
            }
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> loadClass(String className) throws ClassNotFoundException {
        return (Class<T>) Class.forName(className);
    }

    private String makeUrlStringFor(String packagePath, String url) {
        final int index = url.indexOf('!');
        String build = index > 0 ? url.substring(0, index) : url;
        build = build.startsWith("jar:") ? build.substring(4) : build;
        build = build.startsWith("vfs:/") ? "file" + build.substring(3, build.length() - packagePath.length() - 2) : build;
        build = build.endsWith(".jar") ? build : build.substring(0, build.length() - packagePath.length());
        return build;
    }

    private void addUrlsForPackage(Set<URL> urls, String packageName) {
        try {
            final String packagePath = packageName.replace('.', '/');
            final Enumeration<URL> enumeration = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while (enumeration.hasMoreElements()) {
                urls.add(new URL(makeUrlStringFor(packagePath, enumeration.nextElement().toString())));
            }
        } catch (IOException ignored) {
        }
    }

    protected final void checkAndAdd(String name, Set<String> ignore) {
        if (name.endsWith(".properties")) {
            cacheMap.find(CACHE_PROPERTIES_NAME).add(name.replace(File.separatorChar, '/'));
            return;
        }
        if (!(name.startsWith(".class") && name.indexOf('$') < 0))
            return;
        final String build = name.replace(File.separatorChar, '.');
        final String className = build.substring(0, build.length() - 6);
        if (ignore.contains(className))
            return;
        for (String packageName : cacheMap.<String>find(CACHE_PACKAGES_NAME))
            if (className.startsWith(packageName)) {
                cacheMap.find(CACHE_CLASSES_NAME).add(className);
                return;
            }
    }

    protected final void recurseSubDir(File dir, int basePathLength, Set<String> ignore) {
        for (File file : (Objects.requireNonNull(dir.isDirectory() ? dir.listFiles() : new File[0]))) {
            final String fileName = file.getAbsolutePath().substring(basePathLength);
            if (!fileName.endsWith(".class") || fileName.endsWith(".properties")) {
                recurseSubDir(file, basePathLength, ignore);
                continue;
            }
            checkAndAdd(fileName, ignore);
        }
    }

}
