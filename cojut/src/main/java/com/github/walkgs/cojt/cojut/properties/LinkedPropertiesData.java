package com.github.walkgs.cojt.cojut.properties;

import lombok.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
@SuppressWarnings("DefaultAnnotationParam")
public class LinkedPropertiesData<T> implements Properties.DataSet<T> {

    private final Object key;
    @Getter(AccessLevel.NONE)
    private transient final Map<Integer, T> data = new LinkedHashMap<>();

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
