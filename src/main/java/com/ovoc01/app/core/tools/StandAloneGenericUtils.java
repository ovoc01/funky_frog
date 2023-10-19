package com.ovoc01.app.core.tools;

import java.lang.reflect.Field;

import com.ovoc01.app.core.mapping.spec.StandAloneDatabaseAccess;


public class StandAloneGenericUtils {
    public static String columnToSelect(Object object){
        StandAloneDatabaseAccess<Object> s = (StandAloneDatabaseAccess)object;
        StringBuilder q = new StringBuilder();
        
        Field[] fields =  s.getFieldToInsert();
        int length =fields.length;
       for (int i = 0; i < length; i++) {
            q.append(Utils.getColumnName(fields[i]));
            if(i<length-1)q.append(",");
       }
        return q.toString();
    }
}
