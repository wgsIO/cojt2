package com.github.walkgs.cojt.codit.lifecycle.impl;

import com.github.walkgs.cojt.codit.lifecycle.Life;
import com.github.walkgs.cojt.codit.lifecycle.LifeCycleInstaller;
import com.github.walkgs.cojt.codit.lifecycle.LifeDescription;
import com.github.walkgs.cojt.cojut.Applicable;
import com.github.walkgs.cojt.cojys.Cojys;
import com.github.walkgs.cojt.cojys.invokers.strategies.StrategyHandler;
import com.github.walkgs.cojt.cojys.invokers.strategies.StrategyHandlerCreator;
import com.github.walkgs.cojt.cojys.secure.SecureToken;
import lombok.Getter;

@Getter
class LifeDescriptionImpl implements LifeDescription, Applicable<LifeDescriptionImpl> {

    private static final byte[] BUFFER = new byte[64];
    private static final long CODE = SecureToken.generate(BUFFER, false).hashCode() + "LifeDescriptionImpl".hashCode();

    private static final StrategyHandlerCreator STRATEGY_HANDLER_CREATOR = Cojys.getSystemLocalServices().get("StrategyHandlerCreator");

    private static int lastId = 0;
    protected long createdIn = System.currentTimeMillis();
    protected StrategyHandler<LifeDescription, LifeCycleInstaller> strategies = STRATEGY_HANDLER_CREATOR.create();
    protected Object livableObject;
    protected Life life;
    private long id = (lastId++) + CODE + System.currentTimeMillis();
    private LifeCycleInstaller installer;

    protected LifeDescriptionImpl(Object livableObject, LifeCycleInstaller installer) {
        this.livableObject = livableObject;
        this.installer = installer;
    }

    protected void shutdown() {
        id = -1;
        createdIn = -1;
        strategies.shutdown();
        ((LifeImpl) life).shutdown();
        livableObject = null;
        installer = null;
        life = null;
    }

}
