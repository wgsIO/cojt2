package com.github.walkgs.cojt.codit.lifecycle;

import java.util.function.Consumer;

@SuppressWarnings("RedundantThrows")
public interface LifeCycleHandler {

    LifeCycleLoaderConfiguration getConfiguration();

    LifeDescription createObjectLife(Class<?> object, Object... args) throws Exception;

    void load(Life life, Consumer<Life> pre, Consumer<Life> complete);

    void unload(Life life, Consumer<Life> pre, Consumer<Life> complete);

}
