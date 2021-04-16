package com.github.walkgs.cojt.cojys.invokers.posture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    ALIVE(1, Posture.ALIVE) {
        @Override
        public Status next() {
            return DEAD;
        }
    },
    DEAD(-1, Posture.DEAD) {
        @Override
        public Status next() {
            return ALIVE;
        }
    };

    private final int id;
    private final int posture;

    abstract public Status next();

}
