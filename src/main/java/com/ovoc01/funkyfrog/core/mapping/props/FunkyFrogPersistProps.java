package com.ovoc01.funkyfrog.core.mapping.props;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.ovoc01.funkyfrog.core.annotation.ProvideFkOnCreation;
import com.ovoc01.funkyfrog.core.build.FunkyFrogKonstruktor;
import com.ovoc01.funkyfrog.core.mapping.FkObject;
import com.ovoc01.funkyfrog.core.tools.MethodAndParameters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FunkyFrogPersistProps {
    private transient Field primaryKey;
    private transient Field[] fieldToInsert;
    private transient Field[] viewAttribute;
    private transient String table;
    private transient String originTable;
    private transient String database;
    private transient String colString;
    private transient String historyTable;
    private transient HashMap<String, FkObject> fkHashMap;
    
    private transient ProvideFkOnCreation provideFkOnCreation;
    private transient HashMap<String, MethodAndParameters> queryMethodMap;
    private transient String selectTable;
}
