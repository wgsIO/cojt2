package com.github.walkgs.cojt.cojys.invokers.post;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface Powders {

    static void invoke(Object instance, Method[] methods, int type, Object... args) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methods)
            invoke(instance, method, type, args);
    }

    static void invoke(Object instance, Method method, int type, Object... args) throws InvocationTargetException, IllegalAccessException {
        if (!method.isAnnotationPresent(Post.class))
            return;
        final Post annotation = method.getAnnotation(Post.class);
        if ((annotation.type() & type) != type)
            return;
        final boolean oldAccessible = method.isAccessible();
        method.setAccessible(true);
        if (args.length > 0)
            method.invoke(instance, args);
        else
            method.invoke(instance);
        method.setAccessible(oldAccessible);
    }

}
