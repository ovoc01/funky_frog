package com.ovoc01.app.core.mapping.spec;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.ovoc01.app.core.annotation.ProvideFkOnCreation;
import com.ovoc01.app.core.build.QueryBuilder;
import com.ovoc01.app.core.mapping.FkObject;
import com.ovoc01.app.core.tools.StandAloneGenericUtils;
import com.ovoc01.app.core.tools.Utils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StandAloneDatabaseAccess<T> {
    T object;
    private transient Field primaryKey;
    private transient Field[] fieldToInsert;
    private transient Field[] viewAttribute;
    private transient String table;
    private transient String database;
    private transient String colString;
    private transient String historyTable;
    private transient HashMap<String, FkObject> fkHashMap;
    private transient QueryBuilder qBuilder = new QueryBuilder();
    private transient String customPredicate = null;
    private transient ProvideFkOnCreation provideFkOnCreation;

    public StandAloneDatabaseAccess(T oT){
        this.object = oT;
        setTable(Utils.getTableName(object));
        setPrimaryKey(Utils.primaryKey(object));
        setFieldToInsert(Utils.fieldToInsert(object));
        setColString(StandAloneGenericUtils.columnToSelect(this));
        Field[] vFields = Utils.viewAttrFields(object);
        if (vFields != null)
            setViewAttribute(vFields);
        fkHashMap = new HashMap<>();
        provideFkOnCreation = object.getClass().getAnnotation(ProvideFkOnCreation.class);
    }   
    
}
