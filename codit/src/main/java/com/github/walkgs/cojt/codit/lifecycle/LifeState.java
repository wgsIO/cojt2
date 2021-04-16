package com.github.walkgs.cojt.codit.lifecycle;

import com.github.walkgs.cojt.cojys.invokers.posture.Stage;
import com.github.walkgs.cojt.cojys.invokers.posture.Status;

public interface LifeState {

    Stage getStage();

    Status getStatus();

    default int getLifeResult() {
        return getStage().getId() * getStatus().getId();
    }

    default boolean isAlive() {
        return getStatus().getId() == 1;
    }

    default boolean inOperation() {
        return getLifeResult() > 0;
    }

}
