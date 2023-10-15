package com.ovoc01.app.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a field as a column in a database table.
 */
/**
 * Annotation used to specify the mapping of a field to a column in a database table.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SuppressWarnings("unused")
public @interface Column {

    /**
     * The name of the column in the database table. If not specified, the name of the field will be used.
     */
    String name() default "";

    /**
     * Indicates whether the column is a number. Defaults to false.
     */
    boolean isNumber() default false;

    /**
     * The data type of the column in the database table. If not specified, the data type of the field will be used.
     */
    String dataType() default "";
}