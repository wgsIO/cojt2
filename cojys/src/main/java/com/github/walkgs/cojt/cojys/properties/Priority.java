package com.github.walkgs.cojt.cojys.properties;

public interface Priority {

    int HIGHEST = 0x3E9;
    int HIGH = 0x3F2;
    int NORMAL = 0x3E8;
    int LOW = 0x44D;
    int LOWEST = 0x457;

    static boolean isHighest(int priority) {
        return (priority & HIGHEST) == HIGHEST;
    }

    static boolean isHigh(int priority) {
        return (priority & HIGH) == HIGH;
    }

    static boolean isNormal(int priority) {
        return (priority & NORMAL) == NORMAL;
    }

    static boolean isLow(int priority) {
        return (priority & LOW) == LOW;
    }

    static boolean isLowest(int priority) {
        return (priority & LOWEST) == LOWEST;
    }

}
