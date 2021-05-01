package com.github.walkgs.cojt.codit.storage;

import com.github.walkgs.cojt.codit.injector.Nameable;

import java.util.Set;

public interface Cache<T> extends Nameable, Iterable<T> {

    boolean add(T value);

    boolean remove(T value);

    Set<T> getValues();

}
