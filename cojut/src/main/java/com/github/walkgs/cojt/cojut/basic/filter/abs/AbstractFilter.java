package com.github.walkgs.cojt.cojut.basic.filter.abs;

import com.github.walkgs.cojt.cojut.basic.filter.Filter;
import com.github.walkgs.cojt.cojut.basic.filter.TestFilter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public class AbstractFilter<T> implements Filter<T> {

    private final TestFilter<T> test;

    @Override
    public void filter(Consumer<List<T>> result) {
        final T[] elements = test.getElements();
        final Predicate<T> test = this.test.getTest();
        final List<T> results = new ArrayList<>();
        for (T element : elements) {
            if (!test.test(element))
                continue;
            results.add(element);
        }
        result.accept(results);
    }

}
