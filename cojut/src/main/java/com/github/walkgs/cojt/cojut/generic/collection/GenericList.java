package com.github.walkgs.cojt.cojut.generic.collection;

import com.github.walkgs.cojt.cojex.NullArgumentException;
import com.github.walkgs.cojt.cojut.Applicable;

import java.util.Collection;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public class GenericList<T> extends GenericVariety<T> implements GenericCollection<T>, GenericCollection.Set<T>, GenericCollection.List<T>, Applicable<GenericList<T>> {

    @SafeVarargs
    public GenericList(final int capacity, final boolean finals, final T... fictional) {
        super(capacity, finals, fictional);
    }

    @SafeVarargs
    public GenericList(final int capacity, final T... fictional) {
        super(capacity, fictional);
    }

    @Override
    public boolean add(final T value) {
        if (value == null)
            throw new NullArgumentException("value");
        for (int index = 0; index < values.length; index++) {
            if (get(index).isPresent())
                continue;
            set(index, value);
            return true;
        }
        if (autoIncrement()) {
            set(values.length, value);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(final T value) {
        if (value == null)
            throw new NullArgumentException("value");
        for (int index = 0; index < values.length; index++) {
            final Optional<T> result = get(index);
            if (!result.isPresent() || !result.get().equals(value))
                continue;
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public void remove(final int index) {
        if (index >= values.length)
            throw new IllegalArgumentException("The value to be taken does not exist in the set, reason: size of the set smaller than the index.");
        set(index, null);
    }

    @Override
    public boolean contains(final T value) {
        for (int index = 0; index < values.length; index++) {
            final Optional<T> result = get(index);
            if (!result.isPresent() || !result.get().equals(value))
                continue;
            return true;
        }
        return false;
    }

    @Override
    public void set(final int index, final T newValue) {
        super.set(index, newValue);
    }

    @Override
    public Optional<T> get(final int index) {
        return super.value(index);
    }

    @Override
    public void clear() {
        for (int index = 0; index < values.length; index++)
            set(index, null);
    }

    @Override
    public Collection<T> getValues() {
        return super.values();
    }

    @Override
    public boolean isFinals() {
        return super.finals();
    }

    @Override
    public int getCapacity() {
        return super.capacity();
    }

}
