package com.github.walkgs.cojt.cojys.loader.impl;

import com.github.walkgs.cojt.cojys.loader.CJY2Loader;
import lombok.ToString;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@ToString
public class CJY2LoaderImpl extends ClassLoader implements CJY2Loader {

    private static final Queue<CJY2Loader> LOADERS = new ConcurrentLinkedQueue<>();

    {
        LOADERS.add(this);
    }

    public CJY2LoaderImpl(ClassLoader parent) {
        super(parent);
    }

    public CJY2LoaderImpl() {
        super();
    }

    @Override
    public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override
    public Class<?> findClass(final String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    public Class<?> findClass(final String name, final boolean checkAll) throws ClassNotFoundException {
        Class<?> clazz = super.findClass(name);
        if (clazz != null)
            return clazz;
        synchronized (LOADERS) {
            for (CJY2Loader loader : LOADERS) {
                clazz = loader.findClass(name, false);
                if (clazz != null)
                    return clazz;
            }
        }
        return null;
    }

}
