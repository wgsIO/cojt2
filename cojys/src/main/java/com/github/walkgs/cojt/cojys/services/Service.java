package com.github.walkgs.cojt.cojys.services;

import com.github.walkgs.cojt.cojys.properties.Priority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {

    int priority() default Priority.NORMAL;

    Class[] depends() default {};

}
