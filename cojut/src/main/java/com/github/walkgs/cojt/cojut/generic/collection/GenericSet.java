package com.github.walkgs.cojt.cojut.generic.collection;

import com.github.walkgs.cojt.cojut.Applicable;

import java.util.Collection;
import java.util.Optional;

public class GenericSet<T> extends GenericVariety<T> implements GenericCollection<T>, GenericCollection.Set<T>, Applicable<GenericSet<T>> {

    @SafeVarargs
    public GenericSet(final int capacity, final boolean finals, final T... fictional) {
        super(capacity, finals, fictional);
    }

    @SafeVarargs
    public GenericSet(final int capacity, final T... fictional) {
        super(capacity, fictional);
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

    @Override
    public void clear() {
        for (int index = 0; index < values.length; index++)
            set(index, null);
    }

}
