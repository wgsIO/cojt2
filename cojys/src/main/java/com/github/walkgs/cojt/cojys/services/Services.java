package com.github.walkgs.cojt.cojys.services;


import com.github.walkgs.cojt.cojys.services.binder.Binder;

public interface Services<S> extends Binder<S> {

    <C> C get(Class<? extends S> service, Integer... options);

    <C> C get(String name, Integer... options);

}
