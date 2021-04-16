package com.github.walkgs.cojt.cojys.services.binder.data;

import com.github.walkgs.cojt.cojys.properties.Name;
import com.github.walkgs.cojt.cojys.services.binder.Binder;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;

import java.net.BindException;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SingleBinder<T> implements Binder<T> {

    private static final String ALREADY_ASSOCIATED_MESSAGE = "The inserted bind is already linked";
    private static final String NO_ASSOCIATED_MESSAGE = "The inserted bind is not linked";

    private static final String ALREADY_ASSOCIATED_NAME_MESSAGE = "The name you entered has already been used";
    private static final String NO_ASSOCIATED_NAME_MESSAGE = "It was not possible to find any bind using this name";

    private transient List<String> usedNames = new LinkedList<>();
    private transient Queue<Binding<T>> binders = new ConcurrentLinkedQueue<>();

    private transient Binder<Binder<T>> parents;

    protected SingleBinder(Binder<Binder<T>> parents) {
        this.parents = parents;
    }

    public SingleBinder() {
        parents = new SingleBinder<>(null);
    }

    @Override
    public synchronized void bind(final T bind) throws BindException {
        final Class<?> clazz = checkIsAlreadyAssociatedBind(bind);

        if (clazz.isAnnotationPresent(Name.class)) {
            final String name = clazz.getAnnotation(Name.class).name();
            if (usedNames.contains(name))
                throw new BindException(ALREADY_ASSOCIATED_NAME_MESSAGE);
            safeBind(bind, name);
            return;
        }

        String name = UUID.randomUUID().toString();
        while (usedNames.contains(name))
            name = UUID.randomUUID().toString();

        safeBind(bind, name);
    }

    @Override
    public void bind(final Binding<T> bind) throws BindException {
        bind(bind.getBinding(), bind.getName());
    }

    @Override
    public synchronized void bind(final T bind, final String name) throws BindException {
        checkIsAlreadyAssociatedBind(bind);
        if (usedNames.contains(name))
            throw new BindException(ALREADY_ASSOCIATED_NAME_MESSAGE);
        usedNames.add(name);
        binders.add(new Binding<>(name, bind));
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized void unbind(final T bind) throws BindException {
        unbind((Class<? extends T>) bind.getClass());
    }

    @Override
    public synchronized void unbind(final String name) throws BindException {
        for (Binding binding : binders)
            if (binding.getName().equals(name)) {
                binders.remove(binding);
                usedNames.remove(name);
                return;
            }
        throw new BindException(NO_ASSOCIATED_NAME_MESSAGE);
    }

    @Override
    public void unbind(final Binding<T> bind) throws BindException {
        for (Binding binding : binders)
            if (binding == bind) {
                binders.remove(binding);
                usedNames.remove(binding.getName());
                return;
            }
        throw new BindException(NO_ASSOCIATED_MESSAGE);
    }

    @Override
    public synchronized void unbind(final Class<? extends T> bind) throws BindException {
        for (Binding binding : binders)
            if (binding.getBinding().getClass() == bind) {
                binders.remove(binding);
                usedNames.remove(binding.getName());
                return;
            }
        throw new BindException(NO_ASSOCIATED_MESSAGE);
    }

    @Override
    public UnmodifiableIterator<Binding<T>> binders(Integer... options) {
        if (this.parents != null && options.length > 0 && (options[0] & SEARCH_IN_ALL) == SEARCH_IN_ALL) {
            final Set<Binding<T>> binders = Sets.newHashSet(this.binders);
            final UnmodifiableIterator<Binding<Binder<T>>> parents = this.parents.binders(SEARCH_IN_ALL);
            while (parents.hasNext())
                binders.addAll(Lists.newArrayList(parents.next().getBinding().binders()));
            return Iterators.unmodifiableIterator(binders.iterator());
        } else
            return Iterators.unmodifiableIterator(binders.iterator());
    }

    @Override
    public Binder<Binder<T>> parents() {
        return parents;
    }

    public void destroy(final Integer... options) {
        if (parents != null && options.length > 0 && (options[0] & DESTROY_ME_AND_ALL_PARENTS) == DESTROY_ME_AND_ALL_PARENTS)
            if (parents instanceof SingleBinder)
                ((SingleBinder) parents).destroy(DESTROY_ME_AND_ALL_PARENTS);
        usedNames.clear();
        binders.clear();
        usedNames = null;
        binders = null;
        parents = null;
    }

    private Class<?> checkIsAlreadyAssociatedBind(final T bind) throws BindException {
        final Class<?> serviceClass = bind.getClass();
        for (Binding binding : binders)
            if (binding.getBinding().getClass() == serviceClass)
                throw new BindException(ALREADY_ASSOCIATED_MESSAGE);
        return serviceClass;
    }

    private void safeBind(final T bind, final String name) {
        usedNames.add(name);
        binders.add(new Binding<>(name, bind));
    }

}
