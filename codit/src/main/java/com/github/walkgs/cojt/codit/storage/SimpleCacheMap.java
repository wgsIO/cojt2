package com.github.walkgs.cojt.codit.storage;

import com.github.walkgs.cojt.cojut.Applicable;

import java.util.ArrayList;
import java.util.List;

public class SimpleCacheMap implements CacheMap, Applicable<SimpleCacheMap> {

    private final List<Cache<?>> caches = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public <A> Cache<A> find(int index) {
        return (Cache<A>) caches.get(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> Cache<A> find(String name) {
        for (Cache<?> cache : caches)
            if (cache.getName().equals(name))
                return (Cache<A>) cache;
        return null;
    }

    @Override
    public <A> Cache<A> create(String name) {
        return new SimpleCache<A>(name).apply(caches::add);
    }

    @Override
    public <A> Cache<A> createIfAbsent(String name) {
        final Cache<A> cache = find(name);
        if (cache != null)
            return cache;
        return create(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> Cache<A> delete(int index) {
        return (Cache<A>) caches.remove(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A> Cache<A> delete(String name) {
        for (Cache<?> cache : caches)
            if (cache.getName().equals(name)) {
                caches.remove(cache);
                return (Cache<A>) cache;
            }
        return null;
    }

    @Override
    public Cache<?>[] getCaches() {
        return caches.toArray(new Cache[]{});
    }

}
