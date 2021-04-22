package com.github.walkgs.cojt.cojut.function;

import java.util.Objects;

@FunctionalInterface
public interface QuadPredicate<A, B, C, D> {

    boolean test(A a, B b, C c, D d);

    default QuadPredicate<A, B, C, D> and(QuadPredicate<? super A, ? super B, ? super C, ? super D> other) {
        Objects.requireNonNull(other);
        return (a, b, c, d) -> test(a, b, c, d) && other.test(a, b, c, d);
    }

    default QuadPredicate<A, B, C, D> negate() {
        return (a, b, c, d) -> !test(a, b, c, d);
    }

    default QuadPredicate<A, B, C, D> or(QuadPredicate<? super A, ? super B, ? super C, ? super D> other) {
        Objects.requireNonNull(other);
        return (a, b, c, d) -> test(a, b, c, d) || other.test(a, b, c, d);
    }

}