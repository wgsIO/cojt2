package com.github.walkgs.cojt.codit.lifecycle.abs;

import com.github.walkgs.cojt.codit.lifecycle.LifeCycleLoaderConfiguration;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractLoaderConfiguration implements LifeCycleLoaderConfiguration {

    @Getter(AccessLevel.NONE)
    private final Object lock = new Object();

    private transient final Set<Object> defaultStrategies = new LinkedHashSet<>();
    private transient final Set<Object> defaultEvents = new LinkedHashSet<>();

    {
        apply();
    }

    @Override
    public void addStrategy(final Object event) {
        synchronized (lock) {
            if (defaultStrategies.contains(event))
                return;
            defaultStrategies.add(event);
        }
    }

    @Override
    public void addEvent(final Object event) {
        synchronized (lock) {
            if (defaultEvents.contains(event))
                return;
            defaultEvents.add(event);
        }
    }

    @Override
    public void removeStrategy(final Object event) {
        synchronized (lock) {
            if (!defaultStrategies.contains(event))
                return;
            defaultStrategies.remove(event);
        }
    }

    @Override
    public void removeEvent(final Object event) {
        synchronized (lock) {
            if (!defaultEvents.contains(event))
                return;
            defaultEvents.remove(event);
        }
    }

    @Override
    public Object[] getDefaultStrategies() {
        return defaultStrategies.toArray(new Object[]{});
    }

    @Override
    public Object[] getDefaultEvents() {
        return defaultEvents.toArray(new Object[]{});
    }

}
