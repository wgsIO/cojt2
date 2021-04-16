package com.github.walkgs.cojt.cojut;

import java.util.function.Consumer;

public interface Applicable<T extends Applicable> {

    @SuppressWarnings("unchecked")
    default T apply(Consumer<T> apply) {
        final T my = (T) this;
        apply.accept(my);
        return my;
    }

}
