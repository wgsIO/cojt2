package com.github.walkgs.cojt.cojys.invokers.strategies;

import com.github.walkgs.cojt.cojys.executors.Context;

import java.net.BindException;

public interface StrategyHandler<P, A> {

    int SETUP = 0x7919;
    int DISPOSE = 0x759E;

    boolean register(Class<?> strategy) throws BindException;

    boolean register(Object strategy) throws BindException;

    boolean unregister(Class<?> strategy);

    boolean unregister(Object strategy);

    void setup(String name, Context<P, A, StrategyHandler>... context) throws Exception;

    void dispose(String name, Context<P, A, StrategyHandler>... context) throws Exception;

    void setup(String[] strategies, Context<P, A, StrategyHandler>... context) throws Exception;

    void dispose(String[] strategies, Context<P, A, StrategyHandler>... context) throws Exception;

    void setup(Context<P, A, StrategyHandler> context) throws Exception;

    void dispose(Context<P, A, StrategyHandler> context) throws Exception;

    void shutdown();

    String[] strategies();

}
