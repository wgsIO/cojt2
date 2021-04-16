package com.github.walkgs.cojt.codit.handling;

import com.github.walkgs.cojt.cojex.HandlerNotFoundException;
import com.github.walkgs.cojt.cojut.Applicable;
import com.github.walkgs.cojt.cojut.generic.collection.GenericCollection;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
public class HandlerExecutor implements Applicable<HandlerExecutor> {

    private final GenericCollection<Function<Object, Object>> handlers;

    @SuppressWarnings({"unchecked", "RedundantThrows"})
    public <T, R> R execute(T product, int handlerId) throws HandlerNotFoundException {
        final Function<Object, Object> handler = handlers.get(handlerId).orElseThrow(HandlerNotFoundException::new);
        return (R) handler.apply(product);
    }

    @SuppressWarnings({"unchecked", "RedundantThrows"})
    public synchronized <T, R> R executeSync(T product, int handlerId) throws HandlerNotFoundException {
        final Function<Object, Object> handler = handlers.get(handlerId).orElseThrow(HandlerNotFoundException::new);
        return (R) handler.apply(product);
    }

}
