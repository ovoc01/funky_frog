package com.ovoc01.funkyfrog.core.mapping;

import java.sql.Connection;

import com.ovoc01.funkyfrog.core.tools.BackPack;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A class representing a foreign key object used for handling database relationships.
 */
public class FkObject {
    private String fkName;
    private Object fkValue;
    private Class<?> viewClass;
    @Getter
    @Setter
    private boolean init=false;
    /**
     * Default constructor for FkObject.
     */
    public FkObject() {
    }

    /**
     * Constructor for FkObject with specified name, value, and related class.
     *
     * @param name    The name of the foreign key.
     * @param value   The value of the foreign key.
     * @param fkClass The related class associated with the foreign key.
     */
    public FkObject(String name, Object value, Class<?> fkClass,boolean init) {
        setFkName(name);
        setFkValue(value);
        setViewClass(fkClass);
        this.init = init;
    }

    

    /**
     * Initializes the FkObject by performing a database select operation based on the foreign key value.
     *
     * @param c The database connection.
     * @param <T> The type of object to initialize.
     * @return The initialized object based on the foreign key value.
     * @throws Exception If there is an issue during the database operation or object initialization.
     */
    public <T> T init(Connection c) throws Exception {
        FunkyFrogPersist dtbObjectAccess = (FunkyFrogPersist) getViewClass().newInstance();
        Field primaryKey = dtbObjectAccess.getInitializationProperty().getPrimaryKey();
        if (primaryKey == null) {
            throw new IllegalArgumentException("Your object's primary key is null");
        }
        Method setter = dtbObjectAccess.getClass().getDeclaredMethod(BackPack.createSetter(primaryKey.getName()), primaryKey.getType());
        setter.invoke(dtbObjectAccess, fkValue);

        T[] selecTs = dtbObjectAccess.ribbit(c);

        return selecTs[0];
    }

    /**
     * Sets the name of the foreign key.
     *
     * @param fkName The name of the foreign key.
     */
    public void setFkName(String fkName) {
        this.fkName = fkName;
    }

    /**
     * Gets the name of the foreign key.
     *
     * @return The name of the foreign key.
     */
    public String getFkName() {
        return fkName;
    }

    /**
     * Sets the value of the foreign key.
     *
     * @param fkValue The value of the foreign key.
     */
    public void setFkValue(Object fkValue) {
        this.fkValue = fkValue;
    }

    /**
     * Gets the value of the foreign key.
     *
     * @return The value of the foreign key.
     */
    public Object getFkValue() {
        return fkValue;
    }

    /**
     * Sets the related class associated with the foreign key.
     *
     * @param viewClass The related class.
     */
    public void setViewClass(Class<?> viewClass) {
        this.viewClass = viewClass;
    }

    /**
     * Gets the related class associated with the foreign key.
     *
     * @return The related class.
     */
    public Class<?> getViewClass() {
        return viewClass;
    }
}
