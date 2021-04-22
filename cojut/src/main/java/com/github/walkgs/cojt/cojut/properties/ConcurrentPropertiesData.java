package com.github.walkgs.cojt.cojut.properties;

import lombok.*;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class ConcurrentPropertiesData<T> implements Properties.DataSet<T> {

    private final Object key;
    @Getter(AccessLevel.PROTECTED)
    private transient final Map<Integer, T> data = new ConcurrentHashMap<>();

    @Override
    public Properties.DataSet<T> add(final T value) {
        return set(data.size(), value);
    }

    @Override
    public Properties.DataSet<T> set(final int index, final T value) {
        data.put(index, value);
        return this;
    }

    @Override
    public Properties.DataSet<T> remove(final int index) {
        data.remove(index);
        return this;
    }

    @Override
    public T get(final int index) {
        return data.getOrDefault(index, null);
    }

    @Override
    public Collection<T> all() {
        return data.values();
    }

}
