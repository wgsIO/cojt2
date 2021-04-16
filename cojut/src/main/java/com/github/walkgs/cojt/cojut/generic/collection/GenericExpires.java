package com.github.walkgs.cojt.cojut.generic.collection;

import com.github.walkgs.cojt.cojex.NullArgumentException;
import com.google.common.base.Ticker;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public class GenericExpires<T> extends GenericVariety<GenericExpires.Result<T>> implements GenericCollection<T>, GenericCollection.Expires<T> {

    private static final Ticker TICKER = Ticker.systemTicker();

    public GenericExpires(final int capacity, final boolean finals) {
        super(capacity, finals);
        properties.set(3, false);
    }

    public GenericExpires(final int capacity) {
        super(capacity);
        properties.set(3, false);
    }

    @Override
    public boolean add(final T value, long millis) {
        removeExpired();
        if (value == null)
            throw new NullArgumentException("value");
        final Result<T> result = new Result<>(value, (long) (TICKER.read() + (millis * 1e+6)));
        for (int index = 0; index < values.length; index++) {
            if (get(index).isPresent())
                continue;
            set(index, result);
            return true;
        }
        if (autoIncrement()) {
            set(values.length, result);
            return true;
        }
        return false;
    }

    @Override
    protected void set(final int index, final Result<T> newValue) {
        int currentIndex = index;
        checker:
        {
            if (newValue != null && allowEqualsChecker())
                for (int i = 0; i < values.length; i++) {
                    final Result<T> result = value(i).orElse(null);
                    if (result != null && result.value != null && result.value.equals(newValue)) {
                        currentIndex = i;
                        break checker;
                    }

                }
        }
        final Optional<Result<T>> result = value(currentIndex);
        if (result.isPresent() && finals())
            throw new IllegalStateException("The set values cannot be changed.");
        values[currentIndex] = newValue;
    }

    @Override
    public boolean remove(final T value) {
        if (value == null)
            throw new NullArgumentException("value");
        for (int index = 0; index < values.length; index++) {
            final Optional<T> result = get(index, false);
            if (!result.isPresent() || !result.get().equals(value))
                continue;
            remove(index);
            return true;
        }
        return false;
    }

    @Override
    public void remove(final int index) {
        removeExpired();
        if (index >= values.length)
            throw new IllegalArgumentException("The value to be taken does not exist in the set, reason: size of the set smaller than the index.");
        set(index, null);
    }

    @Override
    public boolean contains(final T value) {
        removeExpired();
        for (int index = 0; index < values.length; index++) {
            final Optional<T> result = get(index, false);
            if (!result.isPresent() || !result.get().equals(value))
                continue;
            return true;
        }
        return false;
    }

    @Override
    public Optional<T> get(final int index) {
        return get(index, true);
    }

    private Optional<T> get(final int index, boolean check) {
        if (check)
            removeExpired();
        final Result<T> result = super.value(index).orElse(null);
        return Optional.ofNullable(result != null ? result.value : null);
    }

    @Override
    public void clear() {
        for (int index = 0; index < values.length; index++)
            set(index, null);
    }

    @Override
    public Collection<T> getValues() {
        removeExpired();
        final boolean ignoreNull = ignoreNull();
        final Collection<T> values = new ArrayList<>();
        for (Result<T> result : this.values) {
            if (ignoreNull && (result == null || result.value == null))
                continue;
            values.add(result.value);
        }
        return values;
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
    public String toString() {
        removeExpired();
        return Arrays.toString(getValues().toArray());
    }

    private void removeExpired() {
        boolean readjust = false;
        for (int index = 0; index < values.length; index++) {
            final Result<T> result = value(index).orElse(null);
            if (result == null || !(TICKER.read() > result.millis))
                continue;
            set(index, null);
            readjust = true;
        }
        if (!readjust)
            return;
        readjust();
    }

    private void readjust() {
        final Collection<Result<T>> values = values();
        clear();
        int slot = 0;
        for (Result<T> value : values)
            set(slot++, value);
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    static class Result<T> {

        private final T value;
        private final long millis;

        @Override
        public String toString() {
            return value + "";
        }
    }

}
