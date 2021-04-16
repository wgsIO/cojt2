package com.github.walkgs.cojt.cojut.generic.collection;

import com.github.walkgs.cojt.cojut.properties.LinkedPropertiesData;
import com.github.walkgs.cojt.cojut.properties.Properties;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

@SuppressWarnings("WeakerAccess")
public abstract class GenericVariety<T> {

    final Properties.DataSet<Object> properties = new LinkedPropertiesData<>("genericArray");
    T[] values;

    @SafeVarargs
    @SuppressWarnings("unchecked")
    GenericVariety(int capacity, boolean finals, T... fictional) {
        if (fictional.length > 0)
            throw new IllegalArgumentException("Do not provide values in a fictional argument.");
        if (capacity < 0)
            throw new IllegalArgumentException("The capacity of the pool must be greater than or equal to 0.");

        final Class<?> clazz = fictional.getClass().getComponentType();
        values = (T[]) Array.newInstance(clazz, capacity);

        properties.set(0, true); // ignore null values
        properties.set(1, finals); // values is finals [IGNORE NULL]
        properties.set(2, capacity == 0); // auto increment length
        properties.set(3, true); // allow equals values
    }

    @SafeVarargs
    GenericVariety(int capacity, T... fictional) {
        this(capacity, false, fictional);
    }

    public static Properties.DataSet<Object> findProps(GenericVariety<?> variety) {
        return variety.properties;
    }

    protected void set(int index, T newValue) {
        if (newValue != null && allowEqualsChecker())
            for (T value : values)
                if (value != null && value.equals(newValue))
                    return;
        final Optional<T> result = value(index);
        if (result.isPresent() && finals())
            throw new IllegalStateException("The set values cannot be changed.");
        values[index] = newValue;
    }

    protected Optional<T> value(int index) {
        if (index >= values.length)
            if (autoIncrement())
                values = Arrays.copyOf(values, (index + 1));
            else
                throw new IllegalArgumentException("The value to be taken does not exist in the set, reason: size of the set smaller than the index.");
        return Optional.ofNullable(values[index]);
    }

    protected Collection<T> values(boolean ignoreNull) {
        final Collection<T> values = new ArrayList<>();
        for (T value : this.values) {
            if (ignoreNull && value == null)
                continue;
            values.add(value);
        }
        return values;
    }

    protected Collection<T> values() {
        return values(ignoreNull());
    }

    protected int capacity() {
        return values.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(values().toArray());
    }

    /**
     * PROPERTIES
     **/
    protected boolean ignoreNull() {
        return (boolean) properties.get(0);
    }

    protected boolean finals() {
        return (boolean) properties.get(1);
    }

    protected boolean autoIncrement() {
        return (boolean) properties.get(2);
    }

    protected boolean allowEqualsChecker() {
        return !((boolean) properties.get(3));
    }

}
