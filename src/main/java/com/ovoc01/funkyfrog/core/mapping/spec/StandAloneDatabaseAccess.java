package com.ovoc01.funkyfrog.core.mapping.spec;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.ovoc01.funkyfrog.core.annotation.ProvideFkOnCreation;
import com.ovoc01.funkyfrog.core.build.FunkyFrogKonstruktor;
import com.ovoc01.funkyfrog.core.mapping.FkObject;
import com.ovoc01.funkyfrog.core.tools.StandAloneGenericUtils;
import com.ovoc01.funkyfrog.core.tools.BackPack;

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
    private transient FunkyFrogKonstruktor qBuilder = new FunkyFrogKonstruktor();
    private transient String customPredicate = null;
    private transient ProvideFkOnCreation provideFkOnCreation;

   
    
}
