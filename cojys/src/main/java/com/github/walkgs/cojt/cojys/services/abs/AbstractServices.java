package com.github.walkgs.cojt.cojys.services.abs;

import com.github.walkgs.cojt.cojys.configuration.Configurable;
import com.github.walkgs.cojt.cojys.services.Service;
import com.github.walkgs.cojt.cojys.services.Services;
import com.github.walkgs.cojt.cojys.services.binder.data.Binding;
import com.github.walkgs.cojt.cojys.services.binder.data.SingleBinder;
import com.google.common.collect.UnmodifiableIterator;

import java.net.BindException;

public abstract class AbstractServices<S> extends SingleBinder<S> implements Services<S>, Configurable {

    private static final String NOT_SERVICE_MESSAGE = "The inserted object is not a service";

    {
        apply();
    }

    @Override
    public synchronized void bind(final S bind) throws BindException {
        checkIsServices(bind.getClass());
        super.bind(bind);
    }

    @Override
    public void bind(final Binding<S> bind) throws BindException {
        checkIsServices(bind.getBinding().getClass());
        super.bind(bind);
    }

    @Override
    public synchronized void bind(final S bind, final String name) throws BindException {
        checkIsServices(bind.getClass());
        super.bind(bind, name);
    }

    protected void checkIsServices(Class<?> clazz) throws BindException {
        if (!clazz.isAnnotationPresent(Service.class))
            throw new BindException(NOT_SERVICE_MESSAGE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C get(final Class<? extends S> service, final Integer... options) {
        final UnmodifiableIterator<Binding<S>> binders = binders(options);
        while (binders.hasNext()) {
            final Binding<S> bind = binders.next();
            if (bind.getBinding().getClass() == service)
                return (C) bind.getBinding();
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> C get(final String name, final Integer... options) {
        final UnmodifiableIterator<Binding<S>> binders = binders(options);
        while (binders.hasNext()) {
            final Binding<S> bind = binders.next();
            if (bind.getName().equals(name))
                return (C) bind.getBinding();
        }
        return null;
    }

}
