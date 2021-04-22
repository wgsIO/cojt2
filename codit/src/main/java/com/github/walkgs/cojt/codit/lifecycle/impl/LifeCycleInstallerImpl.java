package com.github.walkgs.cojt.codit.lifecycle.impl;

import com.github.walkgs.cojt.codit.lifecycle.Life;
import com.github.walkgs.cojt.codit.lifecycle.LifeCycle;
import com.github.walkgs.cojt.codit.lifecycle.LifeCycleInstaller;
import com.github.walkgs.cojt.codit.lifecycle.LifeDescription;
import com.github.walkgs.cojt.cojys.Cojys;
import com.github.walkgs.cojt.cojys.executors.Context;
import com.github.walkgs.cojt.cojys.invokers.eventable.EventHandlerCreator;
import com.github.walkgs.cojt.cojys.invokers.posture.Status;
import com.github.walkgs.cojt.cojys.invokers.strategies.StrategyHandler;
import com.github.walkgs.cojt.cojys.services.binder.Binder;
import com.github.walkgs.cojt.cojys.services.binder.data.Binding;
import com.github.walkgs.cojt.cojys.services.binder.data.SingleBinder;
import com.google.common.collect.UnmodifiableIterator;
import com.sun.deploy.association.RegisterFailedException;
import lombok.NonNull;

import java.lang.instrument.IllegalClassFormatException;
import java.net.BindException;

class LifeCycleInstallerImpl implements LifeCycleInstaller {

    protected static final String NO_LIFECYCLE_MESSAGE = "The inserted class does not contain an annotation saying that it is part of the life cycle.";
    private static final String DEFAULT_PRIMARY_NAME = "LifeCycleDescriptionInstaller@";
    private static final EventHandlerCreator EVENT_HANDLER_CREATOR = Cojys.getSystemLocalServices().get("EventHandlerCreator");

    private final SingleBinder<LifeDescription> descriptions = new SingleBinder<>();

    @Override
    public LifeDescription install(final Object instance, final Class<?>... strategies) throws IllegalClassFormatException {
        return install(instance).apply($description -> {
            try {
                final StrategyHandler<LifeDescription, LifeCycleInstaller> strategyHandler = $description.strategies;
                for (Class<?> strategy : strategies)
                    strategyHandler.register(strategy);
                descriptions.bind($description, DEFAULT_PRIMARY_NAME + $description.getCreatedIn());
                strategyHandler.setup(new Context<>($description, this, strategyHandler));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public LifeDescription install(final Object instance, final Object... strategies) throws IllegalClassFormatException {
        return install(instance).apply($description -> {
            try {
                final StrategyHandler<LifeDescription, LifeCycleInstaller> strategyHandler = $description.strategies;
                for (Object strategy : strategies)
                    strategyHandler.register(strategy);
                descriptions.bind($description, DEFAULT_PRIMARY_NAME + $description.getCreatedIn());
                strategyHandler.setup(new Context<>($description, this, strategyHandler));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private LifeDescriptionImpl install(final Object instance) throws IllegalClassFormatException {
        final Class<?> clazz = instance.getClass();
        if (!clazz.isAnnotationPresent(LifeCycle.class))
            throw new IllegalClassFormatException(NO_LIFECYCLE_MESSAGE);
        return new LifeDescriptionImpl(instance, this).apply(description -> {
            description.life = new LifeImpl(0, 0, EVENT_HANDLER_CREATOR.create(), description).apply(life -> {
                life.getState().status = Status.ALIVE;
                life.createdIn = System.currentTimeMillis();
                life.lastChange = life.createdIn;
            });
        });
    }

    @Override
    public void uninstall(final Object instance) {
        uninstall(findDescription(instance));
    }

    @Override
    public void uninstall(@NonNull final LifeDescription description) {
        ((LifeDescriptionImpl) description).apply($description -> {
            try {
                descriptions.unbind(description);
                $description.shutdown();
            } catch (BindException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public LifeDescription findDescription(final Life life) {
        final UnmodifiableIterator<Binding<LifeDescription>> descriptions = this.descriptions.binders(Binder.SEARCH_IN_ALL);
        while (descriptions.hasNext()) {
            final LifeDescription description = descriptions.next().getBinding();
            if (description.getLife() == life)
                return description;
        }
        return null;
    }

    @Override
    public LifeDescription findDescription(final Object instance) {
        final UnmodifiableIterator<Binding<LifeDescription>> descriptions = this.descriptions.binders(Binder.SEARCH_IN_ALL);
        while (descriptions.hasNext()) {
            final LifeDescription description = descriptions.next().getBinding();
            if (description.getLivableObject() == instance)
                return description;
        }
        return null;
    }

    @Override
    public void installEvents(final LifeDescription description, final Class<?>... events) throws BindException {
        final Life life = ((LifeDescriptionImpl) description).life;
        if (!(life instanceof LifeImpl))
            throw new IllegalStateException("");
        final LifeImpl lifeImpl = (LifeImpl) life;
        for (Class<?> event : events)
            lifeImpl.eventHandler.register(event);
    }

    @Override
    public void installEvents(final LifeDescription description, final Object... events) throws BindException {
        final Life life = ((LifeDescriptionImpl) description).life;
        if (!(life instanceof LifeImpl))
            throw new IllegalStateException("");
        final LifeImpl lifeImpl = (LifeImpl) life;
        for (Object event : events)
            lifeImpl.eventHandler.register(event);
    }

}
