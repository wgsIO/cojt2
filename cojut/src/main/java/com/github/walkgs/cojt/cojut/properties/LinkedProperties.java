package com.github.walkgs.cojt.cojut.properties;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
@Data
@ToString
public class LinkedProperties implements Properties {

    @Getter(AccessLevel.NONE)
    private transient final Map<Object, DataSet> dataMap = new LinkedHashMap<>();

    @Override
    public <T> DataSet<T> get(final Object key) {
        final DataSet data = dataMap.get(key);
        return data == null ? new LinkedPropertiesData<>(key) : data;
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
