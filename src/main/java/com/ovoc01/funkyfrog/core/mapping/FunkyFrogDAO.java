
package com.ovoc01.funkyfrog.core.mapping;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.ovoc01.funkyfrog.core.annotation.CustomQuery;
import com.ovoc01.funkyfrog.core.annotation.Mapping;
import com.ovoc01.funkyfrog.core.annotation.PrimaryKey;
import com.ovoc01.funkyfrog.core.annotation.ProvideFkOnCreation;
import com.ovoc01.funkyfrog.core.annotation.ci.InitializationType;
import com.ovoc01.funkyfrog.core.build.FunkyFrogKonstruktor;
import com.ovoc01.funkyfrog.core.connection.FunkyFrogConnexion;
import com.ovoc01.funkyfrog.core.tools.MethodAndParameters;
import com.ovoc01.funkyfrog.core.tools.BackPack;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a database object and provides methods to interact with
 * it.
 * It contains information about the object's primary key, fields to insert,
 * view attributes,
 * table name, database name, column string, history table, foreign key list,
 * and custom predicate.
 * It also provides methods to set and get these attributes, as well as to
 * select all objects
 * from the database and initialize foreign key objects.
 */

@Getter
@Setter
class FunkyFrogDAO implements Serializable {

    private transient Field primaryKey;
    private transient Field[] fieldToInsert;
    private transient Field[] viewAttribute;
    private transient String table;
    private transient String originTable;
    private transient String database;
    private transient String colString;
    private transient String historyTable;
    private transient HashMap<String, FkObject> fkHashMap;
    private transient FunkyFrogKonstruktor qBuilder = new FunkyFrogKonstruktor();
    private transient String customPredicate = null;
    private transient ProvideFkOnCreation provideFkOnCreation;
    private transient HashMap<String, MethodAndParameters> queryMethodMap;
    private transient String selectTable;

    /**
     * Constructs a new instance of DtbObject. It initializes various properties,
     * such as table name,
     * primary key, fields to insert, database name, column selection, view
     * attributes, and foreign key list.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */

    public FunkyFrogDAO() {
        if (getClass().getAnnotation(Mapping.class) != null)
            init();

    }

    private void init() {
        setTable(BackPack.getTableName(this));
        setSelectTable(getClass().getAnnotation(Mapping.class).selectTable());
        provideFkOnCreation = getClass().getAnnotation(ProvideFkOnCreation.class);
        setPrimaryKey(BackPack.primaryKey(this));
        setDatabase(BackPack.getDatabaseName(this));
        try {
            setColString(BackPack.columnToSelect(this));
            setFieldToInsert(BackPack.fieldToInsert(this));
        } catch (IllegalArgumentException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        Field[] vFields = BackPack.viewAttrFields(this);
        if (vFields != null) {
            setViewAttribute(vFields);
        }
        fkHashMap = new HashMap<>();
        setQueryMethodMap(BackPack.getQueryMethods(this));
    }

    /**
     * Sets the array of view attribute fields.
     *
     * @param fields The view attribute fields to set.
     * @throws IllegalArgumentException If view attributes are not found.
     */
    public void setViewAttribute(Field[] fields) {
        if (fields == null)
            throw new IllegalArgumentException("Views attribute not found");
        this.viewAttribute = fields;
    }

    /**
     * Sets the history table name.
     *
     * @throws IllegalArgumentException If a null or empty name is provided.
     */
    public void setHistoryTable() {
        Mapping mapping = getClass().getAnnotation(Mapping.class);
        if (mapping == null)
            throw new IllegalArgumentException("No Mapping annotation found in this object");
        if (mapping.historyTable().equals(""))
            throw new IllegalArgumentException("history table should not equals to empty");
        setHistoryTable(mapping.historyTable());
    }

    /**
     * Gets the history table name.
     *
     */
    public void setHistoryTable(String s) throws IllegalArgumentException {
        if (s == null || s.isEmpty())
            throw new IllegalArgumentException("History table is null or empty");
        this.historyTable = s;

    }

    /**
     * Sets the column string for selection.
     *
     * @param colString The column string to set.
     * @throws IllegalArgumentException If a null or empty string is provided.
     */
    public void setColString(String colString) throws IllegalArgumentException {
        if (colString == null || colString.isEmpty())
            throw new IllegalArgumentException("Column string cannot be null or empty");
        this.colString = colString;
    }

    /**
     * Sets the table name.
     *
     * @param table The table name to set.
     * @throws IllegalArgumentException If a null or empty name is provided.
     */
    public void setTable(String table) throws IllegalArgumentException {
        if (table == null || table.isEmpty())
            throw new IllegalArgumentException("Table name cannot be null or empty");
        this.table = table;
    }

    /**
     * Sets the database name.
     *
     * @param database The database name to set.
     * @throws IllegalArgumentException If a null or empty name is provided.
     */
    public void setDatabase(String database) throws IllegalArgumentException {
        if (database == null || database.isEmpty())
            throw new IllegalArgumentException("Database name cannot be null or empty");
        this.database = database;
    }

    /**
     * Selects and retrieves database objects based on the provided connection.
     *
     * @param c   The database connection.
     * @param <T> The type of the objects to select.
     * @return An array of selected objects.
     * @throws Exception If a database error occurs during the operation.
     */
    public <T> T[] ribbit(Connection c) throws Exception {
        T[] results = null;
        if (c == null) {
            try {
                c = FunkyFrogConnexion.sessionConnection(getDatabase());
                results = (T[]) getAll(c, this.getClass());
                return results;
            } catch (Exception e) {
                throw new Exception(e);
            } finally {
                c.close();
            }
        }
        results = (T[]) getAll(c, this.getClass());
        // init all fk object //TODO
        return results;
    }


    public <T> T ribbitUnik(Connection c) throws Exception {
        T[] result = null;
        if (c == null) {
            try {
                c = FunkyFrogConnexion.sessionConnection(getDatabase());
                result = (T[]) getAll(c, this.getClass());
                return (result.length > 0) ? result[0] : null;
            } catch (Exception e) {
                throw new Exception(e);
            } finally {
                c.close();
            }
        }
        result = (T[]) getAll(c, this.getClass());
        return (result.length > 0) ? result[0] : null;
    }

    /**
     * Retrieves all database objects based on the provided connection and object
     * class.
     *
     * @param c     The database connection.
     * @param clazz The class of the objects to retrieve.
     * @param <T>   The type of the objects to retrieve.
     * @return An array of retrieved objects.
     * @throws Exception If a database error occurs during the operation.
     */
    private <T> T[] getAll(Connection c, Class<T> clazz) throws Exception {
        Statement statement = c.createStatement();
        String query = (customPredicate == null) ? qBuilder.select(this) : customPredicate;
        ResultSet resultSet = statement.executeQuery(query);
        System.out.println(query);
        LinkedList<T> objectLists = new LinkedList<>();
        while (resultSet.next()) {
            FunkyFrogPersist objrT = (FunkyFrogPersist) BackPack.createObject(resultSet, this);
            objrT.initNecessaryForeignKey(c);
            // objrT.setForeignKey(c);
            // objrT.initViewField(c);
            objectLists.add((T) objrT);
        }
        return objectLists.toArray((T[]) Array.newInstance(clazz, objectLists.size()));
    }

    /**
     * Inserts the database object into the database based on the provided
     * connection.
     *
     * @param c The database connection.
     * @throws Exception If a database error occurs during the operation.
     */
    public void croak(Connection c) throws Exception {
        if (c == null) {
            try {
                c = FunkyFrogConnexion.sessionConnection(getDatabase());
                save(c);
                c.commit();
                return;
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally {
                c.close();
            }
        }
        save(c);
    }

    /**
     * Saves the database object into the database based on the provided connection.
     *
     * @param c The database connection.
     * @throws Exception If a database error occurs during the operation.
     */
    private void save(Connection c) throws Exception {
        Statement s = c.createStatement();
        String query = (customPredicate == null) ? new FunkyFrogKonstruktor().insert(this) : customPredicate;
        System.out.println(query);
        s.execute(query);
        s.close();
        resetPredicate();
    }

    /**
     * Resets the custom predicate for database operations.
     */
    private void resetPredicate() {
        this.customPredicate = null;
    }

    /**
     * Modifies the database object in the database based on the provided
     * connection.
     *
     * @param c The database connection.
     * @throws SQLException              If a database error occurs during the
     *                                   operation.
     * @throws IllegalArgumentException  If an invalid argument is provided.
     * @throws IllegalAccessException    If an illegal access occurs during the
     *                                   operation.
     * @throws InvocationTargetException If an invocation error occurs during the
     *                                   operation.
     * @throws NoSuchMethodException     If a required method is not found.
     * @throws SecurityException         If a security error occurs.
     */
    private void modify(Connection c) throws SQLException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        Statement s = c.createStatement();
        String query = (customPredicate == null) ? new FunkyFrogKonstruktor().insert(this) : customPredicate;
        System.out.println(query);
        s.execute(query);
        s.close();
        resetPredicate();
    }

    /**
     * Updates the database object in the database based on the provided connection.
     *
     * @param c The database connection.
     * @throws Exception If a database error occurs during the operation.
     */
    public void leap(Connection c) throws Exception {
        if (c == null) {
            try {
                c = FunkyFrogConnexion.sessionConnection(getDatabase());
                modify(c);
                c.commit();
                return;
            } catch (Exception e) {
                c.rollback();
            } finally {
                c.close();
            }
        }
        modify(c);
    }

    /**
     * Safely updates the database object in the database based on the provided
     * connection and target table name.
     *
     * @param c     The database connection.
     * @param table The target table name.
     * @throws Exception If a database error occurs during the operation.
     */

    public void safeUpdate(Connection c, String table) throws Exception {
        if (c == null)
            throw new Exception(
                    "Connection here should not be null because transaction operation is required for this method");
        setTable(table);
        croak(c);
        leap(c);
        resetTable();
    }

    /**
     * Safely updates the database object in the database based on the provided
     * connection and history table.
     *
     * @param c The database connection.
     * @throws Exception If a database error occurs during the operation.
     */
    public void safeUpdate(Connection c) throws Exception {
        safeUpdate(c, getHistoryTable());
    }

    /**
     * Erases the database object from the database based on the provided
     * connection.
     *
     * @param c The database connection.
     * @throws SQLException              If a database error occurs during the
     *                                   operation.
     * @throws IllegalArgumentException  If an invalid argument is provided.
     * @throws IllegalAccessException    If an illegal access occurs during the
     *                                   operation.
     * @throws InvocationTargetException If an invocation error occurs during the
     *                                   operation.
     * @throws NoSuchMethodException     If a required method is not found.
     * @throws SecurityException         If a security error occurs.
     */
    private void eraseObject(Connection c) throws SQLException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, SecurityException {
        Statement s = c.createStatement();
        String query = (customPredicate == null) ? new FunkyFrogKonstruktor().insert(this) : customPredicate;
        System.out.println(query);
        s.execute(query);
        s.close();
        resetPredicate();
    }

    /**
     * Deletes the database object from the database based on the provided
     * connection.
     *
     * @param c The database connection.
     * @throws Exception If a database error occurs during the operation.
     */
    public void jump(Connection c) throws Exception {
        if (c == null) {
            try {
                c = FunkyFrogConnexion.sessionConnection(getDatabase());
                eraseObject(c);
                c.commit();
                return;
            } catch (Exception e) {
                c.rollback();
                throw e;
            } finally {
                c.close();
            }
        }
        eraseObject(c);
    }

    /**
     * Safely deletes the database object from the database based on the provided
     * connection and target table name.
     *
     * @param c     The database connection.
     * @param table The target table name.
     * @throws Exception If a database error occurs during the operation.
     */
    public void safeDelete(Connection c, String table) throws Exception {
        if (c == null)
            throw new Exception(
                    "Connection here should not be null because transaction operation is required for this method");
        setTable(table);
        croak(c);
        jump(c);
        resetTable();
    }

    /**
     * Retrieves the current sequence value for the primary key of this object.
     *
     * @param c The database connection.
     * @return The current sequence value.
     * @throws RuntimeException If the primary key is null.
     * @throws SQLException     If a SQL error occurs.
     */
    public Integer currentSequenceValue(Connection c) throws RuntimeException, SQLException {
        if (getPrimaryKey() == null)
            throw new RuntimeException("Primary key is null");
        String sequence = getPrimaryKey().getAnnotation(PrimaryKey.class).sequence();
        return BackPack.currentSeqVal(sequence, getPrimaryKey(), c);
    }

    /**
     * Initializes view fields based on the provided connection.
     *
     * @param c The database connection.
     */
    /*
     * public void initViewField(Connection c) {
     * if (getViewAttribute() == null)
     * return;
     * Arrays.stream(getViewAttribute()).forEach(v -> {
     * try {
     * initViewField(c, qBuilder.viewQuery((DtbObjectAccess) this, v), v);
     * } catch (Exception e) {
     * e.printStackTrace();
     * }
     * });
     * }
     */

    /**
     * Initializes a view field based on the provided query and field.
     *
     * @param c     The database connection.
     * @param query The query to retrieve the field value.
     * @param field The field to initialize.
     * @throws IllegalArgumentException If an invalid argument is provided.
     * @throws IllegalAccessException   If an illegal access occurs during the
     *                                  operation.
     * @throws Exception                If an error occurs during initialization.
     */
    @SuppressWarnings({"unused"})
    private void initViewField(Connection c, String query, Field field)
            throws IllegalArgumentException, IllegalAccessException, Exception {
        Class<?> componentType = (field.getType().isArray()) ? field.getType().getComponentType() : field.getType();
        Object[] fieldValue = getAll(c, componentType);
        if (field.getType().isArray()) {
            field.setAccessible(true);
            field.set(this, fieldValue);
            return;
        }
        // var val =(fieldValue.length>0) ? fieldValue[0]: null;
        // field.set(this, val);
    }

    void initNecessaryForeignKey(Connection c) throws Exception {
        if (provideFkOnCreation != null) {
            if (provideFkOnCreation.type() == InitializationType.ALL)
                initAllForeignKey(c);
            else
                initAnnotedForeignKeyOnly(c);
        }
        // TODO
    }

    void initAnnotedForeignKeyOnly(Connection c) throws Exception {
        for (Map.Entry<String, FkObject> mapEntry : getFkHashMap().entrySet()) {
            FkObject fkObject = mapEntry.getValue();
            if (fkObject.isInit()) {
                Object object = fkObject.init(c);
                getClass().getDeclaredMethod(BackPack.createSetter(fkObject.getFkName()), object.getClass()).invoke(
                        this,
                        object);
            }
        }
    }

    public void initForeignKeyByIdentity(String identity, Connection c) throws Exception {
        FkObject fObject = getFkHashMap().get(identity);
        if (fObject == null)
            throw new Exception("the identity of your foreign key does not exist");
        Object object = fObject.init(c);
        getClass().getDeclaredMethod(BackPack.createSetter(fObject.getFkName()), object.getClass()).invoke(this,
                object);
    }

    /**
     * Init all foreign key objects based on the provided connection.
     *
     * @param c The database connection.
     * @throws Exception If an error occurs during foreign key initialization.
     */
    void initAllForeignKey(Connection c) throws Exception {
        for (Map.Entry<String, FkObject> mapEntry : getFkHashMap().entrySet()) {
            FkObject fkObject = mapEntry.getValue();
            Object object = fkObject.init(c);
            getClass().getDeclaredMethod(BackPack.createSetter(fkObject.getFkName()), object.getClass()).invoke(this,
                    object);
        }
    }

    /**
     * Outputs an overview of the object to the console using JSON representation.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void objectOverview() throws InstantiationException, IllegalAccessException {
        System.out.println(BackPack.toJson(this));
    }

    /**
     * Resets the table name to the original table name.
     */
    private void resetTable() {
        setTable(originTable);
    }

    public Object executeDtbQuery(Class<?> clzz, Connection c, String method_identity, Object... args)
            throws Exception {
        MethodAndParameters methodAndParameters = getQueryMethodMap().get(method_identity);
        CustomQuery dtbQuery = methodAndParameters.getMethod().getAnnotation(CustomQuery.class);
        // FunkyFrogPersist obj = (FunkyFrogPersist) clzz.newInstance();
        String query = dtbQuery.value();
        int argsLength = args.length;
        String response = query;
        for (int i = 0; i < argsLength; i++) {
            String placeholder = "\\?" + (i + 1);
            if (args[i] instanceof Number || args[i] instanceof Boolean) {
                response = response.replaceAll(placeholder, String.valueOf(args[i]));
            } else {
                response = response.replaceAll(placeholder, String.format(" '%s' ", args[i]));
            }
        }

        if (clzz.equals(FunkyFrogPersist.class)) {
            return executeFunkyFrogPersist(response, c, methodAndParameters);
        } else {
            return executeSimpleQuery(response, c, methodAndParameters);
        }
    }

    private Object executeFunkyFrogPersist(String query, Connection c, MethodAndParameters methodAndParameters)
            throws Exception {
        FunkyFrogPersist obj = new FunkyFrogPersist();
        obj.setCustomPredicate(query);
        FunkyFrogPersist[] result = obj.ribbit(c);
        if (methodAndParameters.getMethod().getReturnType().isArray()) {
            return result;
        } else {
            return (result[0] == null) ? null : new FunkyFrogPersist[] { result[0] };
        }
    }

    public Object executeSimpleQuery(String query, Connection c, MethodAndParameters methodAndParameters)
            throws SQLException {
        ArrayList<Object> result = new ArrayList<>();
        Statement statement = c.createStatement();
        System.out.println(query);
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            result.add(resultSet.getObject(1));
        }
        if (methodAndParameters.getMethod().getReturnType().isArray()) {
            return result.toArray();
        } else {
            return (result.get(0) == null) ? null : result.get(0);
        }
    }

}