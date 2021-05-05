package com.github.walkgs.cojt.codit.lifecycle;

import com.github.walkgs.cojt.cojys.invokers.strategies.StrategyHandler;

public interface LifeDescription {

    long getId();

    StrategyHandler<LifeDescription, LifeCycleInstaller> getStrategies();

    Object getLivableObject();

    Life getLife();

}
