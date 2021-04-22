package com.github.walkgs.cojt.cojys.invokers.eventable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {

    //base Context<Class<?>, Object[], EventHandler> context
    //protected abstract void whenCalled(Context<Class<? extends Event>, Object[], EventHandler> context, int reasonId);

}
