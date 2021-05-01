package com.github.walkgs.cojt.codit.storage;

import com.github.walkgs.cojt.cojut.Applicable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Getter
@AllArgsConstructor
public class SimpleCache<T> implements Cache<T>, Applicable<SimpleCache<T>> {

    private String name;
    private Set<T> values;

    public SimpleCache(String name) {
        this(name, new HashSet<>());
    }

    @Override
    public boolean add(T value) {
        if (values.contains(value))
            return false;
        return values.add(value);
    }

    @Override
    public boolean remove(T value) {
        if (!values.contains(value))
            return false;
        return values.remove(value);
    }

    @Override
    public Iterator<T> iterator() {
        return getValues().iterator();
    }

}
