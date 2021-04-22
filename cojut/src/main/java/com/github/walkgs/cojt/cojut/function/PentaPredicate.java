package com.github.walkgs.cojt.cojut.function;

import java.util.Objects;

@FunctionalInterface
public interface PentaPredicate<A, B, C, D, E> {

    boolean test(A a, B b, C c, D d, E e);

    default PentaPredicate<A, B, C, D, E> and(PentaPredicate<? super A, ? super B, ? super C, ? super D, ? super E> other) {
        Objects.requireNonNull(other);
        return (a, b, c, d, e) -> test(a, b, c, d, e) && other.test(a, b, c, d, e);
    }

    default PentaPredicate<A, B, C, D, E> negate() {
        return (a, b, c, d, e) -> !test(a, b, c, d, e);
    }

    default PentaPredicate<A, B, C, D, E> or(PentaPredicate<? super A, ? super B, ? super C, ? super D, ? super E> other) {
        Objects.requireNonNull(other);
        return (a, b, c, d, e) -> test(a, b, c, d, e) || other.test(a, b, c, d, e);
    }

}
