package com.github.walkgs.cojt.cojys.invokers.posture;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Stage {

    LOADED(3, Posture.START) {
        @Override
        public Stage next() {
            return UNLOADING;
        }
    },
    UNLOADED(2, Posture.STOP) {
        @Override
        public Stage next() {
            return LOADING;
        }
    },
    LOADING(5, Posture.LOAD) {
        @Override
        public Stage next() {
            return LOADED;
        }
    },
    UNLOADING(4, Posture.UNLOAD) {
        @Override
        public Stage next() {
            return UNLOADED;
        }
    };

    private final int id;
    private final int posture;

    abstract public Stage next();

}
