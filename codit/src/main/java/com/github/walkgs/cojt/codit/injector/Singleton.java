package com.github.walkgs.cojt.codit.injector;

import com.github.walkgs.cojt.cojut.Scope;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Scope
@Documented
@Retention(RUNTIME)
public @interface Singleton {
}
