package com.github.walkgs.cojt.cojut.properties;

import java.util.Collection;

public interface Properties {

    <T> DataSet<T> get(Object key);

    boolean save(DataSet data);

    interface DataSet<T> {

        Object getKey();

        DataSet<T> add(T value);

        DataSet<T> set(int index, T value);

        DataSet<T> remove(int index);

        T get(int index);

        Collection<T> all();

    }

}
