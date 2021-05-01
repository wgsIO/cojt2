package com.github.walkgs.cojt.cojut.reflect;

import com.github.walkgs.cojt.cojut.Scope;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

public interface EasyReflection {

    static Set<String> getContractsFromClass(Class<?> clazz, Class<? extends Annotation> annotation) {
        final Set<String> contractSet = new LinkedHashSet<>();
        if (clazz == null)
            return contractSet;
        contractSet.add(clazz.getName());
        for (Class<?> extend = clazz; extend != null; extend = extend.getSuperclass()) {
            if (extend.isAnnotationPresent(annotation))
                contractSet.add(extend.getName());
            addContractsFromInterfaces(extend.getInterfaces(), contractSet, annotation);
        }
        return contractSet;
    }

    static void addContractsFromInterfaces(Class[] interfaces, Set<String> contractSet, Class<? extends Annotation> annotation) {
        for (Class<?> anInterface : interfaces) {
            if (anInterface.isAnnotationPresent(annotation))
                contractSet.add(anInterface.getName());
            addContractsFromInterfaces(anInterface.getInterfaces(), contractSet, annotation);
        }
    }

    static Annotation getScopeAnnotationFromObject(Object object) {
        if (object == null) throw new IllegalArgumentException();
        return getScopeAnnotationFromClass(object.getClass());
    }

    static Annotation getScopeFromObject(Object object, Annotation defaultAnnotation) {
        return (object != null ? getScopeFromClass(object.getClass(), defaultAnnotation) : defaultAnnotation);
    }

    static Annotation getScopeAnnotationFromClass(Class<?> clazz) {
        if (clazz == null) throw new IllegalArgumentException();
        for (Annotation annotation : clazz.getAnnotations()) {
            final Class<? extends Annotation> type = annotation.annotationType();
            if (type.isAnnotationPresent(Scope.class))
                return annotation;
        }
        return null;
    }

    static Annotation getScopeFromClass(Class<?> clazz, Annotation defaultAnnotation) {
        if (clazz == null) return defaultAnnotation;
        for (Annotation annotation : clazz.getAnnotations()) {
            final Class<? extends Annotation> type = annotation.annotationType();
            if (type.isAnnotationPresent(Scope.class))
                return annotation;
        }
        return defaultAnnotation;
    }

    static void setField(Field field, Object instance, Object value) throws IllegalAccessException {
        setAccessibleState(field, true);
        field.set(instance, value);
    }

    static Object invoke(Object object, Method method, Object args[]) throws InvocationTargetException, IllegalAccessException {
        return invoke(object, method, args, false);
    }

    static Object invoke(Object object, Method method, Object args[], boolean neutral) throws InvocationTargetException, IllegalAccessException {
        if (memberIs(method, Modifier.STATIC))
            object = null;
        if (!method.isAccessible())
            setAccessibleState(method, true);
        final ClassLoader loader = (neutral ? getCurrentContextClassLoader() : null);
        try {
            return method.invoke(object, args);
        } finally {
            if (neutral)
                setContextClassLoader(Thread.currentThread(), loader);
        }
    }

    static void setAccessibleState(final AccessibleObject object, boolean state) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            object.setAccessible(state);
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    static <T> T newInstance(Constructor<?> constructor, Object[] params, boolean neutral) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final ClassLoader loader = (neutral ? getCurrentContextClassLoader() : null);
        try {
            return (T) constructor.newInstance(params);
        } finally {
            if (neutral)
                setContextClassLoader(Thread.currentThread(), loader);
        }
    }

    static void setContextClassLoader(final Thread thread, final ClassLoader loader) {
        AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
            thread.setContextClassLoader(loader);
            return null;
        });
    }

    static ClassLoader getCurrentContextClassLoader() {
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
    }

    static boolean containsAnnotations(Set<Annotation> candidate, Set<Annotation> required) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> candidate.containsAll(required));
    }

    static Class<?> translatePrimitiveType(Class<?> type) {
        final Class<?> translation = Constants.PRIMITIVE_MAP.get(type);
        return (translation != null ? translation : type);
    }

    static boolean memberIs(Member member, int modifier) {
        final int modifiers = member.getModifiers();
        return (modifiers & modifier) == modifier;
    }

    static boolean isFilledIn(ParameterizedType paramType, HashSet<ParameterizedType> recursionKiller) {
        if (recursionKiller.contains(paramType)) return false;
        recursionKiller.add(paramType);

        for (Type type : paramType.getActualTypeArguments()) {
            for (Predicate<Object> predicate : ReflectionObject.FILLING_TESTER)
                if (predicate.test(type))
                    return false;
            if (type instanceof ParameterizedType)
                return isFilledIn((ParameterizedType) type, recursionKiller);
        }

        return true;
    }

    static boolean isFilledIn(ParameterizedType type) {
        return isFilledIn(type, new HashSet<>());
    }

    @SuppressWarnings("unchecked")
    static <C> C cast(Object object) {
        return (C) object;
    }

}
