package com.github.walkgs.cojt.codit.lifecycle;

import com.github.walkgs.cojt.cojys.configuration.Configurable;

public interface LifeCycleLoaderConfiguration extends Configurable {

    Object[] getDefaultStrategies();

    Object[] getDefaultEvents();

    void addStrategy(Object event);

    void addEvent(Object event);

    void removeStrategy(Object event);

    void removeEvent(Object event);

}
