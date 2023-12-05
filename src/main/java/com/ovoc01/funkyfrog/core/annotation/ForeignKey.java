package com.ovoc01.funkyfrog.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ovoc01.funkyfrog.core.annotation.ci.FetchType;
import com.ovoc01.funkyfrog.core.annotation.ci.JoinType;

/**
 * This annotation is used to mark a field as a foreign key in a database table.
 * It is used by the Generic DAO framework to generate SQL queries for CRUD
 * operations.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SuppressWarnings("unused")
public @interface ForeignKey {
    String identity();

    FetchType fetch() default FetchType.LAZY_LOADING;

    JoinType joinType() default JoinType.OneToOne;

    JoinSplate joinSplate() default @JoinSplate(joincolumn = "", mappedBy = "");
}