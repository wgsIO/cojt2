package com.github.walkgs.cojt.codit.handling;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ExecuteForm {

    NORMAL(false),
    SYNC(true);

    public final boolean sync;

}
