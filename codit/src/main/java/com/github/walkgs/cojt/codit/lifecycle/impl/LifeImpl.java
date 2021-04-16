package com.github.walkgs.cojt.codit.lifecycle.impl;

import com.github.walkgs.cojt.codit.lifecycle.Life;
import com.github.walkgs.cojt.codit.lifecycle.LifeCycleHandler;
import com.github.walkgs.cojt.codit.lifecycle.LifeDescription;
import com.github.walkgs.cojt.cojut.Applicable;
import com.github.walkgs.cojt.cojys.invokers.eventable.EventHandler;
import com.github.walkgs.cojt.cojys.invokers.posture.Stage;
import com.github.walkgs.cojt.cojys.invokers.posture.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
class LifeImpl implements Life, Applicable<LifeImpl> {

    private static final String ILLEGAL_STATE_MESSAGE = "The services status must be defined as '%s' and discharged in order to be defined as '%s'; Current Result: {Id: %s, Stage: %s, Status: %s}";

    @Getter
    private final LifeStateImpl state = new LifeStateImpl();

    protected long createdIn;
    protected long lastChange;
    protected EventHandler eventHandler;
    private LifeDescription description;

    protected void nextStage(LifeCycleHandler handler) {
        final Stage next = state.stage.next();
        eventHandler.call(description, handler, next);
        state.stage = next;
    }

    protected void shutdown() {
        state.status = Status.DEAD;
        eventHandler.shutdown();
        state.stage = null;
        description = null;
        eventHandler = null;
        createdIn = -1;
        lastChange = -1;
    }

}
