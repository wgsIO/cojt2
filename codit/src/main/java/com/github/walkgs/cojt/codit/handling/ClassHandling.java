package com.github.walkgs.cojt.codit.handling;

import com.github.walkgs.cojt.cojex.HandlerNotFoundException;
import com.github.walkgs.cojt.cojut.generic.collection.GenericSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import javax.validation.constraints.Size;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ClassHandling {

    //Handlers ID
    int ALL_METHODS = 0;
    int DECLARED_METHODS = 1;

    int ALL_CONSTRUCTORS = 2;
    int DECLARED_CONSTRUCTORS = 3;

    int ALL_FIELDS = 4;
    int DECLARED_FIELDS = 5;

    int NEW_INSTANCE = 6;
    int ALL_METHODS_BY_ANNOTATION = 7;

    @SuppressWarnings("unchecked")
    HandlerExecutor EXECUTOR = new HandlerExecutor(new GenericSet<Function<Object, Object>>(8) {{
        //Simples -> one param
        set(0, object -> ((Class<?>) object).getMethods());
        set(1, object -> ((Class<?>) object).getDeclaredMethods());
        set(2, object -> ((Class<?>) object).getMethods());
        set(3, object -> ((Class<?>) object).getDeclaredMethods());
        set(4, object -> ((Class<?>) object).getFields());
        set(5, object -> ((Class<?>) object).getDeclaredFields());
        //N't Simples -> two+ params
        set(6, object -> {
            final Object[] params = (Object[]) object;
            final Class<?> clazz = (Class<?>) params[0];
            final Object[] arguments = (Object[]) params[1];
            Object instance = null;
            Throwable throwable = null;
            try {
                Constructor<?> constructor = clazz.getConstructor(Arrays.stream(arguments).map(Object::getClass).collect(Collectors.toList()).toArray(new Class[]{}));
                constructor.setAccessible(true);
                instance = constructor.newInstance(arguments);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
                throwable = e;
            }
            return new Object[][]{{instance, throwable}};
        });
        set(7, object -> {
            final Object[] params = (Object[]) object;
            final Iterator<Method> foundMethods = (Iterator<Method>) params[0];
            final Class<? extends Annotation>[] annotations = (Class<? extends Annotation>[]) params[1];
            final HashSet<Method> methods = Sets.newHashSet();
            while (foundMethods.hasNext()) {
                check:
                {
                    final Method method = foundMethods.next();
                    for (Class<? extends Annotation> annotation : annotations)
                        if (!method.isAnnotationPresent(annotation))
                            break check;
                    methods.add(method);
                }
            }
            return methods.toArray();
        });
    }});

    static <T> Iterator<T> simpleGet(Class<?> clazz, @Size(max = 3) int handlingId) throws HandlerNotFoundException {
        return simpleGet(clazz, handlingId, ExecuteForm.NORMAL);
    }

    static <T> Iterator<T> simpleGet(Class<?> clazz, @Size(max = 3) int handlingId, ExecuteForm form) throws HandlerNotFoundException {
        return Iterators.forArray(Objects.requireNonNull(form.sync ? EXECUTOR.executeSync(clazz, handlingId) : EXECUTOR.execute(clazz, handlingId)));
    }

    static <T> Iterator<T> complicatedGet(Object[] params, @Size(min = 3) int handlingId) throws HandlerNotFoundException {
        return complicatedGet(params, handlingId, ExecuteForm.NORMAL);
    }

    static <T> Iterator<T> complicatedGet(Object[] params, @Size(min = 3) int handlingId, ExecuteForm form) throws HandlerNotFoundException {
        return Iterators.forArray(Objects.requireNonNull(form.sync ? EXECUTOR.executeSync(params, handlingId) : EXECUTOR.execute(params, handlingId)));
    }

    //Methods
    static Iterator<Method> getMethodsByAnnotation(Class<?> clazz, @Size(max = 1) int handlingId, Class<? extends Annotation>... annotations) throws HandlerNotFoundException {
        return getMethodsByAnnotation(clazz, handlingId, ExecuteForm.NORMAL, annotations);
    }

    @SafeVarargs
    static Iterator<Method> getMethodsByAnnotation(Class<?> clazz, @Size(max = 1) int handlingId, ExecuteForm form, Class<? extends Annotation>... annotations) throws HandlerNotFoundException {
        return complicatedGet(new Object[]{simpleGet(clazz, handlingId, form), annotations}, ALL_METHODS_BY_ANNOTATION, form);
    }

    @SuppressWarnings("unchecked")
    static <T> T newInstance(Class<? extends T> clazz, Object... initialArgs) throws Exception {
        final StartupForm startupForm = clazz.isAnnotationPresent(StartupForm.class) ? clazz.getAnnotation(StartupForm.class) : null;
        return newInstance(clazz, startupForm != null ? startupForm.form() : ExecuteForm.NORMAL, initialArgs);
    }

    @SuppressWarnings("unchecked")
    static <T> T newInstance(Class<? extends T> clazz, ExecuteForm form, Object... initialArgs) throws Exception {
        final Iterator<Object[]> result = complicatedGet(new Object[]{clazz, initialArgs}, NEW_INSTANCE, form);
        final Object[] data = result.next();
        if (data[1] != null)
            throw ((Exception) data[1]);
        return (T) data[0];
    }

}
