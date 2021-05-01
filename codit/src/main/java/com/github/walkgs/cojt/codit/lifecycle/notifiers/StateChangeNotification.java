package com.github.walkgs.cojt.codit.lifecycle.notifiers;

import com.github.walkgs.cojt.codit.lifecycle.LifeDescription;
import com.github.walkgs.cojt.cojys.executors.Context;
import com.github.walkgs.cojt.cojys.invokers.eventable.Event;
import com.github.walkgs.cojt.cojys.invokers.eventable.EventHandler;
import com.github.walkgs.cojt.cojys.invokers.post.Post;
import com.github.walkgs.cojt.cojys.invokers.posture.Stage;
import com.github.walkgs.cojt.cojys.properties.Name;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Event
@Name(name = "StateChangeNotification")
public class StateChangeNotification {

    @Post(type = EventHandler.WHEN_CALLED)
    private synchronized void onCalled(Context<Class<?>, Object[], EventHandler> context) throws InvocationTargetException, IllegalAccessException {
        final Object[] args = context.getAny();
        final LifeDescription description = parse(args, 0);
        final Stage stage = parse(args, 2);
        final Object instance = description.getLivableObject();
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Post.class))
                continue;
            final Post annotation = method.getAnnotation(Post.class);
            if (annotation.type() != stage.getPosture())
                continue;
            final boolean oldAccessible = method.isAccessible();
            method.setAccessible(true);
            method.invoke(instance);
            method.setAccessible(oldAccessible);
        }
    }

    @SuppressWarnings("unchecked")
    private <C> C parse(Object[] args, int index) {
        return (C) args[index];
    }

}
