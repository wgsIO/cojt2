package com.github.walkgs.cojt.cojut.properties;

import lombok.*;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ToString
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UnmodifiablePropertiesData<T> implements Properties.DataSet<T> {

    private final Object key;
    @Getter(AccessLevel.NONE)
    private transient final Map<Integer, T> data;

    public UnmodifiablePropertiesData(Object key, T... values) {
        this.key = key;
        this.data = new LinkedHashMap<>();
        for (int i = 0; i < values.length; i++)
            data.put(i, values[i]);
    }

    @Override
    public Properties.DataSet<T> add(final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Properties.DataSet<T> set(final int index, final T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Properties.DataSet<T> remove(final int index) {
        throw new UnsupportedOperationException();
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
