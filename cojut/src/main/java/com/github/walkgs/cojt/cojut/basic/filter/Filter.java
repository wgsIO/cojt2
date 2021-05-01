package com.github.walkgs.cojt.cojut.basic.filter;

import java.util.List;
import java.util.function.Consumer;

public interface Filter<T> {

    void filter(Consumer<List<T>> result);

    TestFilter<T> getTest();

}
