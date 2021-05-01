package com.github.walkgs.cojt.cojut.basic.accessor.impl;

import com.github.walkgs.cojt.cojut.basic.accessor.Accessor;
import com.github.walkgs.cojt.cojut.basic.accessor.AccessorType;
import com.github.walkgs.cojt.cojut.basic.accessor.MemberAccessor;
import com.github.walkgs.cojt.cojut.basic.filter.Filter;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
class MemberAccessorImpl implements MemberAccessor {

    private Map<Class<?>, Map<AccessorType, List<Accessor<?>>>> data = new HashMap<>();

    MemberAccessorImpl(Class<?> element) {
        for (Class<?> clazz = element; clazz != null; clazz = clazz.getSuperclass()) {
            final Class<?> $clazz = clazz;
            final Map<AccessorType, List<Accessor<?>>> memberMap = data.computeIfAbsent($clazz, it -> new HashMap<>());
            for (AccessorType type : AccessorType.values())
                memberMap.computeIfAbsent(type, it -> type.operation.apply($clazz));
        }
    }

    @Override
    public <A extends Member> Accessor<A>[] get(AccessorType type) {
        final ArrayList<Accessor<?>> accessors = new ArrayList<>();
        for (Map.Entry<Class<?>, Map<AccessorType, List<Accessor<?>>>> $entry : data.entrySet())
            accessors.addAll($entry.getValue().get(type));
        return accessors.toArray(new Accessor[0]);
    }

    @Override
    public <A extends Member> Accessor<A> get(AccessorType type, int index) {
        return (Accessor<A>) get(type)[0];
    }

    @Override
    public <A extends Member> Accessor<A>[] get(Class<?> clazz, AccessorType type) {
        return data.get(clazz).get(type).toArray(new Accessor[0]);
    }

    @Override
    public <A extends Member> Accessor<A> get(Class<?> clazz, AccessorType type, int index) {
        return (Accessor<A>) get(clazz, type)[0];
    }

    @Override
    public <A extends Member> Accessor<A>[] filter(AccessorType type, Predicate<Accessor<A>> test) {
        return filter(get(type), test);
    }

    @Override
    public <A extends Member> Accessor<A> filter(AccessorType type, int index, Predicate<Accessor<A>> test) {
        return filter(get(type), test)[0];
    }

    @Override
    public <A extends Member> Accessor<A>[] filter(Class<?> clazz, AccessorType type, Predicate<Accessor<A>> test) {
        return filter(get(clazz, type), test);
    }

    @Override
    public <A extends Member> Accessor<A> filter(Class<?> clazz, AccessorType type, int index, Predicate<Accessor<A>> test) {
        return filter(get(clazz, type), test)[0];
    }

    @Override
    public <A extends Member> Accessor<A>[] filter(Accessor<A>[] accessors, Predicate<Accessor<A>> test) {
        final Filter<Accessor<A>> filter = new FilterImpl<>(accessors, test);
        final List<Accessor<A>> values = new ArrayList<>();
        filter.filter(values::addAll);
        return values.toArray(new Accessor[]{});
    }

    @Override
    public void close() throws IOException {
        for (Map.Entry<Class<?>, Map<AccessorType, List<Accessor<?>>>> $entry : data.entrySet())
            for (Map.Entry<AccessorType, List<Accessor<?>>> entry : $entry.getValue().entrySet())
                for (Accessor<?> accessor : entry.getValue())
                    accessor.close();
        data.clear();
        data = null;
    }

}
