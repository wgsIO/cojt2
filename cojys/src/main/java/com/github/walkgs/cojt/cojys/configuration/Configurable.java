package com.github.walkgs.cojt.cojys.configuration;

import com.github.walkgs.cojt.cojys.invokers.post.Powders;
import com.google.common.util.concurrent.ExecutionError;

import java.lang.reflect.InvocationTargetException;

public interface Configurable {

    int CONFIGURATION = 0x4E21;

    default void apply() {
        try {
            Powders.invoke(this, getClass().getDeclaredMethods(), CONFIGURATION);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new ExecutionError(new Error("Could not apply configuration."));
        }
    }

}
