package com.ovoc01.funkyfrog.core.build;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import com.ovoc01.funkyfrog.core.annotation.Column;
import com.ovoc01.funkyfrog.core.annotation.ForeignKey;
import com.ovoc01.funkyfrog.core.mapping.FunkyFrogPersist;
import com.ovoc01.funkyfrog.core.tools.BackPack;

/**
 * The QueryBuilder class provides methods to construct SQL queries for various database operations, such as
 * inserting, selecting, updating, and deleting records in a database. It also assists in constructing
 * queries for viewing database records through specified views.
 */

public class FunkyFrogKonstruktor {

    /**
     * Retrieves an array of fields from the given DtbObjectAccess instance that are
     * not null.
     *
     * @param dtbObjectAccess The DtbObjectAccess instance.
     * @return An array of fields with non-null values.
     */
    public Field[] getNotNullFields(FunkyFrogPersist dtbObjectAccess) {
        return BackPack.notNullFieldsToInsert(dtbObjectAccess, dtbObjectAccess.getInitializationProperty().getFieldToInsert());
    }

    public FunkyFrogKonstruktor() {
    }

    /**
     * Constructs an SQL INSERT query based on the provided object.
     *
     * @param object The object to insert into the database.
     * @return The SQL INSERT query as a string.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws InvocationTargetException If an invocation target exception occurs.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     */
    public String insert(Object object)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        FunkyFrogPersist dtbObjectAccess = (FunkyFrogPersist) object;
        return insert(dtbObjectAccess);
    }

    /**
     * Constructs an SQL INSERT query based on the provided DtbObjectAccess
     * instance.
     *
     * @param dtbObjectAccess The DtbObjectAccess instance to insert into the
     *                        database.
     * @return The SQL INSERT query as a string.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws InvocationTargetException If an invocation target exception occurs.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     */
    public String insert(FunkyFrogPersist dtbObjectAccess)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        Field[] fields = getNotNullFields(dtbObjectAccess);
        StringBuilder query = new StringBuilder();
        query.append(String.format("insert into %s(%s) values(", dtbObjectAccess.getInitializationProperty().getTable(),BackPack.fieldsColumnsName(fields)));
        int fieldsLength = fields.length;
        System.out.println(fieldsLength);
        for (int i = 0; i < fieldsLength; i++) {
            Column column = fields[i].getAnnotation(Column.class);
            ForeignKey fk = fields[i].getAnnotation(ForeignKey.class);
            if (fk != null) {
                fields[i].setAccessible(true);
                FunkyFrogPersist foreignKey = (FunkyFrogPersist) fields[i].get(dtbObjectAccess);
                query.append(helper(foreignKey, foreignKey.getInitializationProperty().getPrimaryKey(),
                        foreignKey.getInitializationProperty().getPrimaryKey().getAnnotation(Column.class)));
            } else {
                query.append(helper(dtbObjectAccess, fields[i], column));
            }
            if (i < fieldsLength - 1)
                query.append(",");
        }
        query.append(")");
        return query.toString();
    }

    /**
     * Joins multiple objects in the database. Not yet implemented.
     *
     * @param primary      The primary object.
     * @param otherObjects Other objects to join with the primary.
     * @return Null, as joining multiple objects is not yet implemented.
     */
    public String joinMultipleObject(Object primary, Object... otherObjects) {
        for (Object object : otherObjects) {
            //joinTwoObject(primary, object);
        }
        return null;
    }

    private String joinTwoObject(FunkyFrogPersist obj, FunkyFrogPersist fKey) {
        
        return null;
    }

    /**
     * Constructs an SQL SELECT query based on the provided object.
     *
     * @param object The object representing the data to be selected.
     * @return The SQL SELECT query as a string.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws IllegalArgumentException  If an argument is illegal.
     * @throws InvocationTargetException If an invocation target exception occurs.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     */
    public String select(Object object) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        FunkyFrogPersist dtbObjectAccess = (FunkyFrogPersist) object;
        return select(dtbObjectAccess);
    }

    /**
     * Constructs an SQL SELECT query based on the provided DtbObjectAccess
     * instance.
     *
     * @param dtbObjectAccess The DtbObjectAccess instance representing the data to
     *                        be selected.
     * @return The SQL SELECT query as a string.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws IllegalArgumentException  If an argument is illegal.
     * @throws InvocationTargetException If an invocation target exception occurs.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     */
    public String select(FunkyFrogPersist dtbObjectAccess) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        Field[] fields = getNotNullFields(dtbObjectAccess);
        StringBuilder query = new StringBuilder();
        String table = dtbObjectAccess.getInitializationProperty().getSelectTable().equals("") ? dtbObjectAccess.getInitializationProperty().getTable()
                : dtbObjectAccess.getInitializationProperty().getSelectTable();
        query.append(String.format("select %s from %s ", dtbObjectAccess.getInitializationProperty().getColString(), table));
        if (fields.length < 1)
            return query.toString();
        else {
            int fieldsLength = fields.length;
            StringBuilder predicate = new StringBuilder();
            predicate.append(" where ");
            for (int i = 0; i < fieldsLength; i++) {
                String columnName = BackPack.getColumnName(fields[i]);

                Column column = fields[i].getAnnotation(Column.class);
                predicate.append(columnName).append("=").append(helper(dtbObjectAccess, fields[i], column));
                ;

                if (i < fieldsLength - 1)
                    predicate.append(" and ");
            }
            query.append(predicate);
        }
        return query.toString();
    }

    public <T extends FunkyFrogPersist> String selectWithJointures(T object){
        
        return null;
    }

    /**
     * Constructs an SQL UPDATE query based on the provided object.
     *
     * @param object The object representing the data to be updated.
     * @return The SQL UPDATE query as a string.
     * @throws IllegalArgumentException  If an argument is illegal.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws InvocationTargetException If an invocation target exception occurs.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     */
    public String update(Object object) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        return update((FunkyFrogPersist) object);
    }

    /**
     * Constructs an SQL UPDATE query based on the provided DtbObjectAccess
     * instance.
     *
     * @param dtbObjectAccess The DtbObjectAccess instance representing the data to
     *                        be updated.
     * @return The SQL UPDATE query as a string.
     * @throws IllegalArgumentException  If an argument is illegal.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws InvocationTargetException If an invocation target exception occurs.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     */
   
    public String update(FunkyFrogPersist dtbObjectAccess) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, SecurityException {

        Field[] columnToUpdate = getNotNullFields(dtbObjectAccess);
        StringBuilder stBuilder = new StringBuilder();
        StringBuilder predicate = new StringBuilder();
        Field primaryKey = dtbObjectAccess.getInitializationProperty().getPrimaryKey();
        boolean pkPresence = (primaryKey != null);

        int fieldsLength = columnToUpdate.length;
        stBuilder.append(String.format("update %s set ", dtbObjectAccess.getInitializationProperty().getTable()));
        predicate.append(" where ");
        if (fieldsLength < 1)
            throw new IllegalArgumentException(
                    "This object have not a field to update due to null value of all attribute");
        if (pkPresence) {
            String pkName = BackPack.getColumnName(primaryKey);
            Column pkColumn = primaryKey.getAnnotation(Column.class);
            predicate.append(String.format("%s=%s", pkName, helper(dtbObjectAccess, primaryKey, pkColumn)));
        }
        for (int i = 0; i < fieldsLength; i++) {
            String columnName = BackPack.getColumnName(columnToUpdate[i]);

            Column column = columnToUpdate[i].getAnnotation(Column.class);
            String lineString = String.format("%s = %s", columnName,
                    helper(dtbObjectAccess, columnToUpdate[i], column));
            stBuilder.append(lineString);
            if (!pkPresence)
                predicate.append(lineString);
            if (i < fieldsLength - 1) {
                stBuilder.append(" , ");
                if (!pkPresence)
                    predicate.append(" and ");
            }
        }
        stBuilder.append(predicate);
        return stBuilder.toString();
    }

    /**
     * Constructs an SQL DELETE query based on the provided object.
     *
     * @param object The object representing the data to be deleted.
     * @return The SQL DELETE query as a string.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws InvocationTargetException If an invocation target exception occurs.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     */

    public String delete(Object object)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        return delete((FunkyFrogPersist) object);
    }

     /**
     * Constructs an SQL DELETE query based on the provided DtbObjectAccess
     * instance.
     *
     * @param dtbObjectAccess The DtbObjectAccess instance representing the data to
     *                        be deleted.
     * @return The SQL DELETE query as a string.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws InvocationTargetException If an invocation target exception occurs.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     */
    public String delete(FunkyFrogPersist dtbObjectAccess)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("delete from %s where ", dtbObjectAccess.getInitializationProperty().getTable()));
        Field primaryKey = dtbObjectAccess.getInitializationProperty().getPrimaryKey();
        if (primaryKey == null)
            throw new IllegalArgumentException("To delete an object , it should have an primary key");

        String pkName = BackPack.getColumnName(primaryKey);
        Column pkColumn = primaryKey.getAnnotation(Column.class);
        stringBuilder.append(String.format("%s=%s", pkName, helper(dtbObjectAccess, primaryKey, pkColumn)));

        return stringBuilder.toString();
    }

    /**
     * Helper method to construct specific parts of SQL queries based on field
     * values.
     *
     * @param object The object containing the field value.
     * @param field  The field for which to construct the query part.
     * @param column The Column annotation for the field.
     * @return The query part as a string.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws InvocationTargetException If an invocation target exception occurs.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     */
    private static Object helper(Object object, Field field, Column column)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
        String fieldName = field.getName();
        if (column.isNumber())
            return object.getClass().getMethod(BackPack.createGetter(fieldName)).invoke(object);
        else
            return String.format("'%s'",
                    object.getClass().getMethod(BackPack.createGetter(fieldName)).invoke(object));
    }

    /**
     * Constructs a sequence query to retrieve the next value from a specified
     * sequence.
     *
     * @param sequence The name of the sequence.
     * @return The sequence query as a string.
     */
    public static String buildSequenceQuery(String sequence) {
        return String.format("select nextval('%s')", sequence);
    }

    /**
     * Constructs a SQL SELECT query for viewing records through a specified view.
     *
     * @param object The DtbObjectAccess object representing the view.
     * @param view   The view Field for which to construct the query.
     * @return The SQL SELECT query for the view as a string.
     * @throws IllegalArgumentException If the argument is illegal or null.
     * @throws IllegalAccessException   If access to a class is denied.
     */
    /* public String viewQuery(DtbObjectAccess object, Field view)
            throws IllegalArgumentException, IllegalAccessException {

        if (view == null)
            throw new IllegalArgumentException("The argument you try to pass i null");

        Class<?> componentType = (view.getType().isArray()) ? view.getType().getComponentType() : null;
        if (componentType != null)
            System.out.println(componentType);

        View viewAnnotation = view.getAnnotation(View.class);
        String orderBy = "";
        if (viewAnnotation == null)
            throw new IllegalArgumentException("The object you try to pass is not annoted @View");

        if (viewAnnotation.orderBy().length > 0) {
            StringBuilder orderByPredicate = new StringBuilder();
            Arrays.stream(viewAnnotation.orderBy()).forEach(
                    predicate -> orderByPredicate.append(predicate).append(","));
            orderByPredicate.deleteCharAt(orderByPredicate.lastIndexOf(","));

            orderBy = (orderByPredicate.toString() == "" || orderByPredicate.toString() == null) ? orderBy
                    : String.format("order by %s", orderByPredicate.toString());
        }

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(String.format("select * from %s %s where %s", viewAnnotation.view(), orderBy,
                primaryKeyPredicat(object, object.getPrimaryKey())));
        return sBuilder.toString();
    } */

    /**
     * Constructs a SQL predicate for the primary key field in the context of a
     * DELETE or VIEW query.
     *
     * @param pObject The object representing the data for which the predicate is
     *                constructed.
     * @param field   The primary key field for which the predicate is constructed.
     * @return The SQL predicate as a string.
     * @throws IllegalArgumentException If the provided field is null.
     * @throws IllegalAccessException   If access to a class is denied.
     */
    
    private String primaryKeyPredicat(Object pObject, Field field)
            throws IllegalArgumentException, IllegalAccessException {
        if (field == null)
            return "";
        Column column = field.getAnnotation(Column.class);
        field.setAccessible(true);
        if (column == null)
            throw new IllegalArgumentException("colum is not present in this field");
        Object pkValue = (column.isNumber()) ? String.format("%s", field.get(pObject))
                : String.format("'%s'", field.get(pObject));
        return String.format("%s=%s", BackPack.getColumnName(field), pkValue);
    }

}

