package com.github.walkgs.cojt.cojys.properties;

public interface Exchanger<T> {

    T request(String message);

    void toExchange(T object, String message);

}
