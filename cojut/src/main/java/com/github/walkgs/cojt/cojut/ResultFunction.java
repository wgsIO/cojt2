package com.github.walkgs.cojt.cojut;

import lombok.AccessLevel;
import lombok.Setter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;


@Setter
@SuppressWarnings("WeakerAccess")
public class ResultFunction<T, R> implements Applicable<ResultFunction<T, R>> {

    private T product;
    private R result;

    @Setter(AccessLevel.NONE)
    transient private Queue<Consumer<T>> consumerList = new ConcurrentLinkedQueue<>();

    public void execute(Consumer<T> consumer) {
        consumerList.add(consumer);
    }

    public R getResult() {
        final R result = this.result;
        consumerList.forEach(it -> it.accept(product));
        this.result = null;
        this.product = null;
        this.consumerList = null;
        return result;
    }

}
