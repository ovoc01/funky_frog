package com.ovoc01.app.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to specify the view name, maximum size, order by and filters for a field in a DAO class.
 * The view name is a required attribute and must be specified.
 * The maximum size attribute is optional and defaults to 1.
 * The order by and filters attributes are optional and can be specified as arrays of strings.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SuppressWarnings("unused")
public @interface View {
    /**
     * The SQL view definition for the database object.
     *
     * @return The SQL view definition.
     */
    String view();

    /**
     * The maximum number of rows to return from the view (default is 1).
     *
     * @return The maximum number of rows.
     */
    int maxSize() default 1;

    /**
     * An array of columns or expressions to order the view by (default is empty).
     *
     * @return The columns or expressions for ordering.
     */
    String[] orderBy() default {};

    /**
     * An array of filter conditions to apply to the view (default is empty).
     *
     * @return The filter conditions for the view.
     */
    String[] filters() default {};
}