package com.github.walkgs.cojt.cojut.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@ToString
public class ConcurrentProperties implements Properties {

    @Getter(AccessLevel.PROTECTED)
    private transient final Map<Object, DataSet> dataMap = new ConcurrentHashMap<>();

    @Override
    public <T> DataSet<T> get(final Object key) {
        final DataSet data = dataMap.get(key);
        return data == null ? new ConcurrentPropertiesData<>(key) : data;
    }

    @Override
    public boolean save(final DataSet data) {
        final boolean[] result = new boolean[1];
        dataMap.computeIfAbsent(data.getKey(), it -> {
            result[0] = true;
            return data;
        });
        return result[0];
    }

}
