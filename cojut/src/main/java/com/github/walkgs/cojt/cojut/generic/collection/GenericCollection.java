package com.github.walkgs.cojt.cojut.generic.collection;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

public interface GenericCollection<T> extends Iterable<T> {

    Optional<T> get(final int index);

    Collection<T> getValues();

    default int getSize() {
        return getValues().size();
    }

    default Optional<T> getFirst() {
        final java.util.List<T> values = (java.util.List<T>) getValues();
        final int size = values.size();
        return Optional.ofNullable(size > 0 ? values.get(0) : null);
    }

    default Optional<T> getLast() {
        final java.util.List<T> values = (java.util.List<T>) getValues();
        final int size = values.size();
        return Optional.ofNullable(size > 0 ? values.get(size - 1) : null);
    }

    boolean isFinals();

    int getCapacity();

    void clear();

    @Nonnull
    default Iterator<T> iterator() {
        return getValues().iterator();
    }

    /**
     * -- LISTS --
     *
     * @param <T>
     */
    interface List<T> extends Set<T> {

        boolean add(T value);

        boolean remove(T value);

        void remove(int index);

        boolean contains(final T value);

    }

    /**
     * -- SETS --
     *
     * @param <T> object set
     */
    interface Expires<T> extends GenericCollection<T> {

        boolean add(T value, long millis);

        boolean remove(T value);

        void remove(int index);

        boolean contains(final T value);

    }

    /**
     * -- SETS --
     *
     * @param <T> object set
     */
    interface Set<T> extends GenericCollection<T> {

        void set(final int index, final T newValue);

    }

}
