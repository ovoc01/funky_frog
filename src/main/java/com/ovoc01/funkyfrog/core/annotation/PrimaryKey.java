package com.ovoc01.funkyfrog.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field is a primary key in a database table.
 * This annotation should be used on fields that represent primary keys in a database table.
 * The `sequence` attribute specifies the name of the sequence used to generate the primary key value.
 * The `prefix` attribute specifies a prefix to be added to the generated primary key value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SuppressWarnings("unused")

public @interface PrimaryKey{
    /**
     * The sequence name for generating the primary key.
     *
     * @return The name of the sequence.
     */
    String sequence();

    /**
     * The prefix to be added to the generated primary key.
     *
     * @return The prefix string.
     */
    String prefix();
    
}