package com.ovoc01.app.core.tools;

import com.ovoc01.app.core.annotation.*;

import com.ovoc01.app.core.build.QueryBuilder;
import com.ovoc01.app.core.mapping.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;;

    /**
     * This class provides utility methods for the application.
     */
public class Utils {

    /**
     * function how get the table name 
     * @params Object the object
     * @return String the table name
     */
    public static String getTableName(Object object) throws RuntimeException{
        Mapping mapping = object.getClass().getAnnotation(Mapping.class);
        if(mapping ==null) throw new RuntimeException("Mapping annotation not found");
        if(mapping.table().isEmpty()) return object.getClass().getSimpleName().toLowerCase();
        return mapping.table().toLowerCase();
    }

    /**
     * Returns the comma-separated column names for the given fields.
     *
     * @param fields the fields to get the column names for
     * @return the comma-separated column names
     */
    public static String fieldsColumnsName(Field[] fields){
        StringBuilder stringBuilder = new StringBuilder();
        int fieldLength = fields.length;
        for (int i = 0; i < fieldLength; i++) {
            stringBuilder.append(getColumnName(fields[i]));
            if(i<fieldLength-1) stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }

    /**
     * function how get the database name 
     * @params Object the object
     * @return String the database name
     */
    public static String getDatabaseName(Object object) throws RuntimeException{
        Mapping mapping = object.getClass().getAnnotation(Mapping.class);
        if(mapping ==null) throw new RuntimeException("Mapping annotation not found");
        if(mapping.database().isEmpty()) throw new RuntimeException("Database name cannot be empty");
        return mapping.database().toLowerCase();
    }
    
    /**
     * function who retrieve the primary key object of a class if it exist
     * @params Object the object
     * @return Field the primary key 
     */
    public static Field primaryKey (Object object){
       return Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(PrimaryKey.class)).findFirst().orElse(null);  
    }

    /**
     * Returns the fields annotated with @Column for the given object.
     *
     * @param object the object to get the fields for
     * @return the fields annotated with @Column
     */
    public static Field[] fieldToInsert(Object object){
        return Arrays.stream(object.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(Column.class)).toArray(Field[]::new);
    }

    /**
     * Checks if the given field is a primary key.
     *
     * @param field the field to check
     * @return the PrimaryKey annotation for the field
     * @throws RuntimeException if the field is not a primary key
     */
    public static PrimaryKey  isPrimaryKey(Field field) throws RuntimeException{
        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
        if(primaryKey==null) throw new RuntimeException("The field "+field.getName()+" is not a primary key");
        return primaryKey;
    }

    /**
     * Returns the current working directory.
     *
     * @return the current working directory
     */
    public static String currentLocation(){
        return System.getProperty("user.dir");
    }

    /**
     * Returns the current value of the specified sequence.
     *
     * @param sequence the name of the sequence
     * @param field the primary key field of the table associated with the sequence
     * @param c the database connection
     * @return the current value of the sequence
     * @throws RuntimeException if the connection is null
     * @throws SQLException if an error occurs while executing the query
     */
    public static Integer currentSeqVal(String sequence, Field field, Connection c) throws RuntimeException, SQLException {
        if (c == null) {
            throw new RuntimeException("Connection cannot be null, please create a connection");
        }
        isPrimaryKey(field);
        String query = QueryBuilder.buildSequenceQuery(sequence);
        Statement statement = c.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            return resultSet.getInt(1);
        }
        return null;
    }

    /**
     * Returns a string with leading zeros to match the given length.
     *
     * @param nbr the number to pad with zeros
     * @param pk the prefix to add before the number
     * @param nbrL the total length of the resulting string
     * @return the padded string
     */
    public static String completeZero(int nbr, String pk, int nbrL) {
        StringBuilder value = new StringBuilder();
        int length = nbrL - (String.valueOf(nbr).length() + pk.length());
        for (int i = 0; i < length; i++) {
            value.append("0");
        }
        return value.toString() + nbr;
    }

    /**
     * Constructs a string sequence for the given object using the primary key field and sequence name.
     *
     * @param object the object to construct the sequence for
     * @param c the database connection
     * @param nbrL the total length of the resulting string
     * @return the constructed string sequence
     * @throws Exception if an error occurs while constructing the sequence
     */
    public static String constructBasicStringSequence(Object object, Connection c, int nbrL) throws Exception {
        Field primaryKey = primaryKey(object);
        PrimaryKey pKey = isPrimaryKey(primaryKey);
        Integer seq = currentSeqVal(pKey.sequence(), primaryKey, c);
        if (seq != null)
            return completeZero(seq, pKey.prefix(), nbrL);
        return null;
    }
    
    /**
     * Builds a string primary key using the given separator and arguments.
     *
     * @param separator the separator to use between arguments
     * @param args the arguments to use in the primary key
     * @return the constructed string primary key
     */
    public static String buildStringPk(String separator,String ...args){
        StringBuilder pk = new StringBuilder();
        int length = args.length;
        for (int i = 0; i < length; i++) {
            pk.append(args[i]);
            if(i!=length-1) pk.append(separator);
        }
        return pk.toString();
    }

    /**
     * Returns the fields annotated with @Column that are not null for the given object.
     *
     * @param object the object to get the fields for
     * @param fields the fields to check for null values
     * @return the fields annotated with @Column that are not null
     */
    public static Field[] notNullFieldsToInsert(Object object,Field[] fields){
        return Arrays.stream(fields)
                .filter(field -> {
                    field.setAccessible(true);
                    try {
                        return field.get(object) != null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                })
                .toArray(Field[]::new);
    }

    /**
     * Constructs a getter method name for the given field name.
     *
     * @param field the field name to construct the getter for
     * @return the constructed getter method name
     */
    public static String createGetter(String field){
        return "get"+field.substring(0,1).toUpperCase()+field.substring(1);
    }

    /**
     * Creates the setter method name for a field by capitalizing the first letter
     * of the field name and prefixing it with "set."
     *
     * @param field The name of the field.
     * @return The setter method name.
     */    
    public static String createSetter(String field){
        return "set"+field.substring(0,1).toUpperCase()+field.substring(1);
    }

    /**
     * Gets the column name for a field based on its associated Column annotation.
     * If no custom name is specified, the field name is used.
     *
     * @param field The field for which to retrieve the column name.
     * @return The column name.
     */
     public static String getColumnName(Field field){
        Column column = field.getAnnotation(Column.class);
        if(column.name().equals("")) return field.getName();
        return column.name();
    }

    /**
     * Generates a comma-separated list of column names for a given object based on
     * its fields that should be inserted into the database.
     *
     * @param object The database object for which to generate the column list.
     * @return A string containing the comma-separated column names.
     */
    public static String columnToSelect(Object object){
        DtbObjectAccess dtbObjectAccess = (DtbObjectAccess)object;
        StringBuilder q = new StringBuilder();
        Field[] fields =  dtbObjectAccess.getFieldToInsert();
        int length =fields.length;
       for (int i = 0; i < length; i++) {
            q.append(Utils.getColumnName(fields[i]));
            if(i<length-1)q.append(",");
       }
        return q.toString();
    }




    /**
     * Creates and populates an object based on the data from a ResultSet.
     *
     * @param rSet   The ResultSet containing the data to populate the object.
     * @param object The object to be populated with data.
     * @return An object populated with data from the ResultSet.
     * @throws SQLException              If a database access error occurs.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws IllegalArgumentException  If an illegal argument is passed.
     * @throws InvocationTargetException If an invocation target exception occurs.
     * @throws InstantiationException    If an instantiation error occurs.
     */
    public static Object createObject(ResultSet rSet, Object object)
            throws SQLException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        DtbObjectAccess dtbObjectAccess = (DtbObjectAccess) object;
        DtbObjectAccess returnValue = dtbObjectAccess.getClass().newInstance();
        Field[] fields = dtbObjectAccess.getFieldToInsert();
        for (Field field : fields) {
            String colString = Utils.getColumnName(field);
            Object value = rSet.getObject(colString);
            checkIfObjectIsFk(dtbObjectAccess, returnValue, field, value, colString);
        }
        return returnValue;
    }

    /**
     * Checks if an object is a foreign key and adds it to the return value if so,
     * otherwise, sets the value using reflection.
     *
     * @param dtbObjectAccess The object to access the data.
     * @param returnValue     The object to return.
     * @param field           The field being processed.
     * @param value           The value retrieved from the ResultSet.
     * @param colString       The column name.
     * @throws NoSuchMethodException     If a requested method is not found.
     * @throws SecurityException         If a security violation occurs.
     * @throws IllegalAccessException    If access to a class is denied.
     * @throws IllegalArgumentException  If an illegal argument is passed.
     * @throws InvocationTargetException If an invocation target exception occurs.
     */
    private static void checkIfObjectIsFk(DtbObjectAccess dtbObjectAccess, DtbObjectAccess returnValue, Field field,
            Object value,
            String colString) throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ForeignKey foreign = field.getAnnotation(ForeignKey.class);
        if (foreign != null) {
            FkObject fkObject = new FkObject(colString, value, field.getType(),foreign.initialization());
            returnValue.getFkHashMap().put(foreign.identity(), fkObject);
        } else {
            Class<?> valClass = (value != null) ? value.getClass() : field.getType();
            Method setter = dtbObjectAccess.getClass().getDeclaredMethod(Utils.createSetter(field.getName()), valClass);
            setter.invoke(returnValue, value);
        }
    }

    /**
     * Converts an object to a JSON representation.
     *
     * @param obj The object to be converted to JSON.
     * @param <T> The type of the object.
     * @return A JSON string representing the object.
     */
    public static <T> String toJson(T obj) {
        StringBuilder jsonBuilder = new StringBuilder("{");
        Class<?> objClass = obj.getClass();
        Field[] fields = objClass.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                fieldValue = "Error accessing field";
            }

            jsonBuilder.append(fieldName).append(":");
            if (fieldValue != null) {
                jsonBuilder.append("\"").append(fieldValue).append("\"");
            } else {
                jsonBuilder.append("null");
            }

            if (i < fields.length - 1) {
                jsonBuilder.append(",");
            }
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    /**
     * Retrieve an array of fields annotated with a 'View' annotation from the
     * provided object.
     *
     * @param object The object from which to extract view attribute fields.
     * @return An array of fields annotated with 'View'.
     */
    public static Field[] viewAttrFields(Object object) {
        return Arrays.stream(object.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(View.class))
                .toArray(Field[]::new);
    }

}