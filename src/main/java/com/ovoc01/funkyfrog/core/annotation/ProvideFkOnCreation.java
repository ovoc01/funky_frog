package com.ovoc01.funkyfrog.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ovoc01.funkyfrog.core.annotation.ci.InitializationType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SuppressWarnings("unused")
public @interface ProvideFkOnCreation {
    InitializationType type() default InitializationType.ALL;
}
