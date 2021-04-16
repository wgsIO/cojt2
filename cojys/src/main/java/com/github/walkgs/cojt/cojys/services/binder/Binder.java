package com.github.walkgs.cojt.cojys.services.binder;

import com.github.walkgs.cojt.cojys.services.binder.data.Binding;
import com.google.common.collect.UnmodifiableIterator;

import java.net.BindException;

public interface Binder<T> {

    int SEARCH_IN_ALL = 0x3EA;
    int DESTROY_ME_AND_ALL_PARENTS = 0x4EB;

    void bind(T bind) throws BindException;

    void bind(Binding<T> bind) throws BindException;

    void bind(T bind, String name) throws BindException;

    void unbind(T bind) throws BindException;

    void unbind(String name) throws BindException;

    void unbind(Binding<T> bind) throws BindException;

    void unbind(Class<? extends T> bind) throws BindException;

    UnmodifiableIterator<Binding<T>> binders(Integer... options);

    Binder<Binder<T>> parents();

}
