package com.github.walkgs.cojt.cojut.properties;

import lombok.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@ToString
@RequiredArgsConstructor
public class UnmodifiableProperties implements Properties {

    @Getter(AccessLevel.NONE)
    private transient final Map<Object, DataSet> dataMap;

    public UnmodifiableProperties(DataSet... sets) {
        this.dataMap = new LinkedHashMap<>();
        for (DataSet set : sets)
            dataMap.put(set.getKey(), set);
    }

    @Override
    public <T> DataSet<T> get(final Object key) {
        final DataSet data = dataMap.get(key);
        return data == null ? new UnmodifiablePropertiesData<>(key, new LinkedHashMap<>()) : data;
    }

    @Override
    public boolean save(final DataSet data) {
        throw new UnsupportedOperationException();
    }

}
