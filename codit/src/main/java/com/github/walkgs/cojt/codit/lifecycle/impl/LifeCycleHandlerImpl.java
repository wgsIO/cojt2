package com.github.walkgs.cojt.codit.lifecycle.impl;

import com.github.walkgs.cojt.codit.handling.ClassHandling;
import com.github.walkgs.cojt.codit.lifecycle.*;
import com.github.walkgs.cojt.codit.lifecycle.abs.AbstractLoaderConfiguration;
import com.github.walkgs.cojt.codit.lifecycle.notifiers.StateChangeNotification;
import com.github.walkgs.cojt.cojys.invokers.post.Post;
import com.github.walkgs.cojt.cojys.invokers.posture.Stage;
import com.github.walkgs.cojt.cojys.properties.Exchanger;
import lombok.Getter;

import java.lang.instrument.IllegalClassFormatException;
import java.util.function.Consumer;


public class LifeCycleHandlerImpl implements LifeCycleHandler, Exchanger<LifeCycleInstaller> {

    private static final String INCORRECT_LIFE_STATE_MESSAGE = "Could not load, reason: The state of life is incorrect.";

    private static final LifeCycleLoaderConfiguration DEFAULT_CONFIGURATION = new AbstractLoaderConfiguration() {

        @Post(type = CONFIGURATION)
        private void configure() {
            addEvent(new StateChangeNotification());
        }

    };

    @Getter
    private final LifeCycleLoaderConfiguration configuration = DEFAULT_CONFIGURATION;
    private final LifeCycleInstaller installer = new LifeCycleInstallerImpl();

    @Override
    public LifeDescription createObjectLife(final Class<?> object, final Object... args) throws Exception {
        if (!object.isAnnotationPresent(LifeCycle.class))
            throw new IllegalClassFormatException(LifeCycleInstallerImpl.NO_LIFECYCLE_MESSAGE);
        final Object instance = ClassHandling.newInstance(object, args);
        final LifeDescription description = installer.install(instance, configuration.getDefaultStrategies());
        installer.installEvents(description, configuration.getDefaultEvents());
        return description;
    }

    @Override
    public void load(final Life life, final Consumer<Life> pre, final Consumer<Life> complete) {
        final LifeState state = life.getState();
        if (!state.isAlive() || state.getStage() != Stage.UNLOADED)
            throw new IllegalStateException(INCORRECT_LIFE_STATE_MESSAGE);
        ((LifeImpl) life).nextStage(this);
        pre.accept(life);
        ((LifeImpl) life).nextStage(this);
        complete.accept(life);
    }

    @Override
    public void unload(final Life life, final Consumer<Life> pre, final Consumer<Life> complete) {
        final LifeState state = life.getState();
        if (!state.isAlive() || state.getStage() != Stage.LOADED)
            throw new IllegalStateException(INCORRECT_LIFE_STATE_MESSAGE);
        ((LifeImpl) life).nextStage(this);
        pre.accept(life);
        ((LifeImpl) life).nextStage(this);
        complete.accept(life);
    }

    @Override
    public LifeCycleInstaller request(final String message) {
        return installer;
    }

    @Override
    public void toExchange(final LifeCycleInstaller object, final String message) {
    }

}
