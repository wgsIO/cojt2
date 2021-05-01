package com.github.walkgs.cojt.cojut.basic.accessor;

import java.io.Closeable;
import java.lang.reflect.Member;
import java.util.function.Predicate;

public interface MemberAccessor extends Closeable, AutoCloseable {

    <A extends Member> Accessor<A>[] get(AccessorType type);

    <A extends Member> Accessor<A> get(AccessorType type, int index);

    <A extends Member> Accessor[] get(Class<?> clazz, AccessorType type);

    <A extends Member> Accessor<A> get(Class<?> clazz, AccessorType type, int index);

    <A extends Member> Accessor<A>[] filter(AccessorType type, Predicate<Accessor<A>> test);

    <A extends Member> Accessor<A> filter(AccessorType type, int index, Predicate<Accessor<A>> test);

    <A extends Member> Accessor<A>[] filter(Class<?> clazz, AccessorType type, Predicate<Accessor<A>> test);

    <A extends Member> Accessor<A> filter(Class<?> clazz, AccessorType type, int index, Predicate<Accessor<A>> test);

    <A extends Member> Accessor<A>[] filter(Accessor<A>[] accessors, Predicate<Accessor<A>> test);

}
