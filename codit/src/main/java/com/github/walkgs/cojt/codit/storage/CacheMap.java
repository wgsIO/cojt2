package com.github.walkgs.cojt.codit.storage;

public interface CacheMap {

    <A> Cache<A> find(int index);

    <A> Cache<A> find(String name);

    <A> Cache<A> create(String name);

    <A> Cache<A> createIfAbsent(String name);

    <A> Cache<A> delete(int index);

    <A> Cache<A> delete(String name);

    <A> Cache<A>[] getCaches();

}
