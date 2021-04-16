package com.github.walkgs.cojt.codit.handling;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StartupForm {

    ExecuteForm form() default ExecuteForm.NORMAL;

}
