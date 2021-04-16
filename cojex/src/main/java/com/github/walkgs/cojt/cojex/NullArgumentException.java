package com.github.walkgs.cojt.cojex;

public class NullArgumentException extends IllegalArgumentException {

    public NullArgumentException(String argName) {
        super((argName == null ? "Argument" : argName) + " must not be null.");
    }

}
