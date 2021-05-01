package com.github.walkgs.cojt.cojys.services.binder.data;

import com.github.walkgs.cojt.cojys.services.binder.Binder;
import com.google.common.collect.UnmodifiableIterator;
import lombok.AccessLevel;
import lombok.Getter;

import java.net.BindException;

public class UnmodifiableSingleBinder<T> implements Binder<T> {

    @Getter(AccessLevel.PROTECTED)
    private transient final Binder<T> binder = new SingleBinder<>();

    public UnmodifiableSingleBinder(Binder<T> binder, Integer... options) throws BindException {
        final UnmodifiableIterator<Binding<T>> binders = binder.binders(options);
        while (binders.hasNext())
            this.binder.bind(binders.next());
    }

    @SafeVarargs
    public UnmodifiableSingleBinder(T... binders) throws BindException {
        for (T bind : binders)
            binder.bind(bind);
    }

    @Override
    public void bind(final T bind) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bind(final Binding<T> binding) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void bind(final T bind, final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unbind(final T bind) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unbind(final String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unbind(final Binding<T> binding) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unbind(final Class<? extends T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public UnmodifiableIterator<Binding<T>> binders(final Integer... options) {
        return binder.binders(options);
    }

    @Override
    public Binder<Binder<T>> parents() {
        return binder.parents();
    }

}
