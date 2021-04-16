package com.github.walkgs.cojt.cojys.invokers.strategies;

public interface StrategyHandlerCreator {

    <A, B> StrategyHandler<A, B> create();

}
