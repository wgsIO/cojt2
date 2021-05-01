package com.github.walkgs.cojt.codit.lifecycle;

import java.lang.instrument.IllegalClassFormatException;
import java.net.BindException;

public interface LifeCycleInstaller {

    LifeDescription install(Object instance, Class<?>... strategies) throws IllegalClassFormatException;

    LifeDescription install(Object instance, Object... strategies) throws IllegalClassFormatException;

    void uninstall(Object instance);

    void uninstall(LifeDescription description);

    LifeDescription findDescription(Life life);

    LifeDescription findDescription(Object instance);

    void installEvents(LifeDescription description, Class<?>... events) throws BindException;

    void installEvents(LifeDescription description, Object... events) throws BindException;

}
