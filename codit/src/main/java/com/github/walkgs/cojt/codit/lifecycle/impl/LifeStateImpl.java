package com.github.walkgs.cojt.codit.lifecycle.impl;

import com.github.walkgs.cojt.codit.lifecycle.LifeState;
import com.github.walkgs.cojt.cojys.invokers.posture.Stage;
import com.github.walkgs.cojt.cojys.invokers.posture.Status;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
class LifeStateImpl implements LifeState {

    protected Stage stage = Stage.UNLOADED;
    protected Status status = Status.DEAD;

}
