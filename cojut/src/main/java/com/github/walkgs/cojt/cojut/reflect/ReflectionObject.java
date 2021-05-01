package com.github.walkgs.cojt.cojut.reflect;

import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

interface ReflectionObject {

    Object LOCK = new Object();
    Set<Predicate<Object>> FILLING_TESTER = new LinkedHashSet<Predicate<Object>>() {
        {
            add((object) -> object instanceof TypeVariable);
            add((object) -> object instanceof WildcardType);
        }
    };

}
