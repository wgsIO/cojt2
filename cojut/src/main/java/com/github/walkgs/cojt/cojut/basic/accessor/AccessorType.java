package com.github.walkgs.cojt.cojut.basic.accessor;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@RequiredArgsConstructor
public enum AccessorType {

    FIELDS($clazz -> make($clazz.getDeclaredFields())),
    METHODS($clazz -> make($clazz.getDeclaredMethods())),
    CONSTRUCTORS($clazz -> make($clazz.getDeclaredConstructors()));

    public final Function<Class<?>, List<Accessor<?>>> operation;

    private static List<Accessor<?>> make(Member[] members) {
        final ClassAccessor provider = ClassAccessor.getProvider();
        final List<Accessor<?>> accessors = new ArrayList<>();
        for (Member member : members)
            accessors.add(provider.createAccessor(member));
        return accessors;
    }

}
