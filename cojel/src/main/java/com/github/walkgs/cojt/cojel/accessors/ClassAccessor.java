package com.github.walkgs.cojt.cojel.accessors;

import com.github.walkgs.cojt.cojys.secure.SecureToken;

public interface ClassAccessor {

    <T> AccessorQuery<T> query(Class<? extends T> clazz);

    <T> AccessorQuery<T> query(Class<? extends T> clazz, String name);

    <T> AccessorQuery<T> query(Class<? extends T> clazz, SecureToken.Token identifier);

    <T> AccessorQuery<T> query(Class<? extends T> clazz, AccessorConfiguration configuration);

    <T> AccessorQuery<T> query(Class<? extends T> clazz, String name, AccessorConfiguration configuration);

    <T> AccessorQuery<T> query(Class<? extends T> clazz, SecureToken.Token identifier, AccessorConfiguration configuration);

    <T> AccessorQuery<T> find(SecureToken.Token identifier);

    AccessorQuery[] queries();

}
