package com.github.walkgs.cojt.cojut.basic.filter;

import java.util.function.Predicate;

public interface TestFilter<T> {

    T[] getElements();

    Predicate<T> getTest();

}
