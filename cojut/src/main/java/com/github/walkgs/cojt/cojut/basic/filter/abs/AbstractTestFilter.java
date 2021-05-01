package com.github.walkgs.cojt.cojut.basic.filter.abs;

import com.github.walkgs.cojt.cojut.basic.filter.TestFilter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public class AbstractTestFilter<T> implements TestFilter<T> {

    private final T[] elements;
    private final Predicate<T> test;

}
