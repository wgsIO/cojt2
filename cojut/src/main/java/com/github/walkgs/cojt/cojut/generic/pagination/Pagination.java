package com.github.walkgs.cojt.cojut.generic.pagination;

import com.github.walkgs.cojt.cojut.generic.collection.GenericCollection;

import java.util.List;
import java.util.Optional;

public interface Pagination<T> {

    int getLimit();

    int getPageCapacity();

    List<GenericCollection.Set<T>> getPages();

    GenericCollection.List<T> add();

    Optional<GenericCollection.Set<T>> get(int page);

    boolean remove(GenericCollection.Set<T> page);

    GenericCollection.Set<T> remove(int page);

    interface Iterator<T> extends Pagination<T> {

        int getCurrentPage();

        GenericCollection.Set<T> next();

        Optional<GenericCollection.Set<T>> current();

        GenericCollection.Set<T> previous();

        boolean hasNext();

        boolean isLast();

        boolean isFirst();

    }

}
