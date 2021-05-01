package com.github.walkgs.cojt.cojut.basic.accessor.impl;

import com.github.walkgs.cojt.cojut.basic.accessor.Accessor;
import com.github.walkgs.cojt.cojut.basic.filter.abs.AbstractFilter;
import com.github.walkgs.cojt.cojut.basic.filter.abs.AbstractTestFilter;

import java.util.function.Predicate;

class FilterImpl<T extends Accessor<?>> extends AbstractFilter<T> {

    FilterImpl(T[] accessors, Predicate<T> predicate) {
        super(new AbstractTestFilter<>(accessors, predicate));
    }

}
