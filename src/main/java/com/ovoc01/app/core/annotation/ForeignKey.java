package com.ovoc01.app.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark a field as a foreign key in a database table.
 * It is used by the Generic DAO framework to generate SQL queries for CRUD operations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SuppressWarnings("unused")
public @interface ForeignKey {
    String identity();
    boolean initialization() default false;
}