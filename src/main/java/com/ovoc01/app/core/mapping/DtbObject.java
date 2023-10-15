
package com.ovoc01.app.core.mapping;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.ovoc01.app.core.annotation.*;
import com.ovoc01.app.core.annotation.ci.InitializationType;
import com.ovoc01.app.core.build.QueryBuilder;
import com.ovoc01.app.core.connection.DtbConnexion;
import com.ovoc01.app.core.tools.Utils;

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
class DtbObject implements Serializable {

    private transient Field primaryKey;
    private transient Field[] fieldToInsert;
    private transient Field[] viewAttribute;
    private transient String table;
    private transient final String originTable;
    private transient String database;
    private transient String colString;
    private transient String historyTable;
    private transient HashMap<String, FkObject> fkHashMap;
    private transient QueryBuilder qBuilder = new QueryBuilder();
    private transient String customPredicate = null;
    private transient ProvideFkOnCreation provideFkOnCreation;

    /**
     * Constructs a new instance of DtbObject. It initializes various properties,
     * such as table name,
     * primary key, fields to insert, database name, column selection, view
     * attributes, and foreign key list.
     */

    public DtbObject() {
        setTable(Utils.getTableName(this));
        originTable = getTable();
        setPrimaryKey(Utils.primaryKey(this));
        setFieldToInsert(Utils.fieldToInsert(this));
        setDatabase(Utils.getDatabaseName(this));
        setColString(Utils.columnToSelect(this));
        Field[] vFields = Utils.viewAttrFields(this);
        if (vFields != null)
            setViewAttribute(vFields);
        fkHashMap = new HashMap<>();
        provideFkOnCreation = this.getClass().getAnnotation(ProvideFkOnCreation.class);
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
            throw new IllegalArgumentException("ColString is null");
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
    public <T> T[] select(Connection c) throws Exception {
        T[] results = null;
        if (c == null) {
            try {
                c = DtbConnexion.sessionConnection(getDatabase());
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
        LinkedList<T> objectLists = new LinkedList<>();
        while (resultSet.next()) {
            DtbObjectAccess objrT = (DtbObjectAccess) Utils.createObject(resultSet, this);
            objrT.initNecessaryForeignKey(c);
            // objrT.setForeignKey(c); //TODO : not obligatoire
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
    public void insert(Connection c) throws Exception {
        if (c == null) {
            try {
                c = DtbConnexion.sessionConnection(getDatabase());
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
        String query = (customPredicate == null) ? new QueryBuilder().insert(this) : customPredicate;
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
        String query = (customPredicate == null) ? new QueryBuilder().insert(this) : customPredicate;
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
    public void update(Connection c) throws Exception {
        if (c == null) {
            try {
                c = DtbConnexion.sessionConnection(getDatabase());
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
        insert(c);
        update(c);
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
        String query = (customPredicate == null) ? new QueryBuilder().insert(this) : customPredicate;
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
    public void delete(Connection c) throws Exception {
        if (c == null) {
            try {
                c = DtbConnexion.sessionConnection(getDatabase());
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
        insert(c);
        delete(c);
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
        return Utils.currentSeqVal(sequence, getPrimaryKey(), c);
    }

    /**
     * Initializes view fields based on the provided connection.
     *
     * @param c The database connection.
     */
    public void initViewField(Connection c) {
        if (getViewAttribute() == null)
            return;
        Arrays.stream(getViewAttribute()).forEach(v -> {
            try {
                initViewField(c, qBuilder.viewQuery((DtbObjectAccess) this, v), v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

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
                getClass().getDeclaredMethod(Utils.createSetter(fkObject.getFkName()), object.getClass()).invoke(this,
                        object);
            }
        }
    }

    public void initForeignKeyByIdentity(String identity, Connection c) throws Exception {
        FkObject fObject = getFkHashMap().get(identity);
        if (fObject == null)
            throw new Exception("the identity of your foreign key does not exist");
        Object object = fObject.init(c);
        getClass().getDeclaredMethod(Utils.createSetter(fObject.getFkName()), object.getClass()).invoke(this,
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
            getClass().getDeclaredMethod(Utils.createSetter(fkObject.getFkName()), object.getClass()).invoke(this,
                    object);
        }
    }

    /**
     * Outputs an overview of the object to the console using JSON representation.
     */
    public void objectOverview() {
        System.out.println(Utils.toJson(this));
    }

    /**
     * Resets the table name to the original table name.
     */
    private void resetTable() {
        setTable(originTable);
    }
}