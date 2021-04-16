package com.github.walkgs.cojt.cojut.element;

import lombok.Data;

@Data
public class ObjectLinker<T, A> {

    private final T object;
    private final A linked;

}
