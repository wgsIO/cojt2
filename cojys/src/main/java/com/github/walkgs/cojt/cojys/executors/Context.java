package com.github.walkgs.cojt.cojys.executors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper = false)
public class Context<P, A, E> {

    private final P provider;
    private final A any;

    private final E executor;

    // P provider();

    // A any();

    // E executor();

}
