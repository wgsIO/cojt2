package com.github.walkgs.cojt.cojex;

import java.io.IOException;

public class HandlerNotFoundException extends IOException {

    public HandlerNotFoundException() {
        super("The entered handing id was not found or not registered.");
    }

}
