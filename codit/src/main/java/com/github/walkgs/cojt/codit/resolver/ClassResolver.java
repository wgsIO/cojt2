package com.github.walkgs.cojt.codit.resolver;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.SortedSet;

public interface ClassResolver {

    <T> Set<Class<T>> getSubClassesOf(Class<T> clazz);

    <T> Set<Class<T>> getSubClassesAnnotatedOf(Class<T> clazz, Class<? extends Annotation> annotation);

    <T> Set<Class<T>> getClassesAnnotated(Class<? extends Annotation> annotation);

    <T> Set<Class<T>> getClassesAnnotatedItems(Class<? extends Annotation> annotation);

    <T> Set<Class<T>> getSubClassesOf(Class<T> clazz, Set<String> ignore);

    <T> Set<Class<T>> getSubClassesAnnotatedOf(Class<T> clazz, Set<String> ignore, Class<? extends Annotation> annotation);

    <T> Set<Class<T>> getClassesAnnotated(Set<String> ignore, Class<? extends Annotation> annotation);

    <T> Set<Class<T>> getClassesAnnotatedItems(Set<String> ignore, Class<? extends Annotation> annotation);

    SortedSet<String> getProperties(String path);

}
