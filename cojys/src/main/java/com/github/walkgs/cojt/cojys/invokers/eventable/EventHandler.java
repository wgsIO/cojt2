package com.github.walkgs.cojt.cojys.invokers.eventable;

import com.github.walkgs.cojt.cojys.executors.Context;
import com.github.walkgs.cojt.cojys.invokers.post.Powders;
import com.github.walkgs.cojt.cojys.services.binder.data.Binding;

import java.lang.reflect.InvocationTargetException;
import java.net.BindException;

public interface EventHandler {

    int WHEN_CALLED = 0x7D01;
    //int UNKNOWN_NOTIFY = 0x7D15;

    //List<Integer> REASONS = new ImmutableObservableList<Integer>() {{
    //    set(0, 0x100);
    //    set(1, UNKNOWN_NOTIFY);
    //}};

    static void call(Binding<Object> event, Context<Class<?>, Object[], EventHandler> context, int reason) {
        try {
            final Class<?> clazz = context.getProvider();
            if (!clazz.isAnnotationPresent(Event.class))
                throw new IllegalArgumentException("The data entered for the event is not compatible");
            Powders.invoke(event.getBinding(), clazz.getDeclaredMethods(), reason, context);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalAccessError("An error occurred while calling the event; Possible reasons: 'incomparable method' or 'access denied to the method'");
        }
    }

    void register(Class<?> event) throws BindException;

    void register(Object event) throws BindException;

    void unregister(Class<?> event) throws BindException;

    void unregister(Object event) throws BindException;

    void call(Object... args);

    void call(Class<?> event, Object... args);

    void call(String name, Object... args);

    void call(Class<?> event, Object[] args, int reason);

    void call(String name, Object[] args, int reason);

    void call(Class<?> event, Object[] args, int reason, boolean byType);

    void shutdown();

}
