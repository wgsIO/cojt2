package com.github.walkgs.cojt.cojel.accessors;

import com.github.walkgs.cojt.cojel.accessors.type.AccessibleType;
import com.github.walkgs.cojt.cojel.accessors.type.AccessorGetterType;
import com.github.walkgs.cojt.cojut.element.ObjectLinker;
import com.github.walkgs.cojt.cojys.secure.SecureToken;

import java.io.Closeable;
import java.lang.reflect.AccessibleObject;
import java.util.List;

@SuppressWarnings("UnusedReturnValue")
public interface AccessorQuery<T> extends Closeable {

    Connection<T> open();

    SecureToken.Token getIdentifier();

    Class<? extends T> getType();

    AccessorQuery<T> cluster(AccessibleType type) throws IllegalAccessException;

    AccessorQuery<T> cluster(Connection<T> connection, AccessibleType type) throws IllegalAccessException;

    AccessorQuery<T> cluster(Connection<T> connection, ObjectLinker<AccessibleObject, String> object, AccessibleType type) throws IllegalAccessException;

    AccessorQuery<T> cluster(Connection<T> connection, List<ObjectLinker<AccessibleObject, String>> objects, AccessibleType type) throws IllegalAccessException;

    interface Connection<T> extends Closeable {

        AccessibleObject get(String name);

        AccessibleObject get(int index, AccessibleType type);

        AccessibleObject get(String name, AccessibleType type);

        Applicable<T> applicable(int index, AccessibleType type);

        Applicable<T> applicable(String name, AccessibleType type);

        Applicable<T> applicable(AccessibleObject accessible, AccessibleType type);

        void copy(ObjectLinker<AccessibleObject, String> object, AccessibleType type, int permission) throws IllegalAccessException;

        void copy(List<ObjectLinker<AccessibleObject, String>> objects, AccessibleType type, int permission) throws IllegalAccessException;

    }

    interface Applicable<T> extends Closeable {

        <A> A get() throws Exception;

        <A> A get(AccessorGetterType getterType) throws Exception;

        int getModifiers();

        @SuppressWarnings("RedundantThrows")
        int assignModifier(int modifier) throws NoSuchFieldException, IllegalAccessException;

        <A> A apply(final Object... values) throws Exception;

        <A> A apply(T instance, final Object... values) throws Exception;

    }

}
