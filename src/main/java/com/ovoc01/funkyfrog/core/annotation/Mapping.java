package com.ovoc01.funkyfrog.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to specify the mapping of a class to a database table.
 * It can also be used to specify the history table for the class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SuppressWarnings("unused")
public @interface Mapping {
    /**
     * The name of the database table to which the annotated class is mapped.
     * @return The name of the database table.
     */
    String table() default "";

    /**
     * The name of the database to which the annotated class is mapped.
     * @return The name of the database.
     */
    String database();

    /**
     * The name of the history table for the annotated class.
     * @return The name of the history table.
     */
    String historyTable() default "";

    String selectTable() default "";
}