package com.ovoc01.app.core.mapping;

import java.sql.Connection;

import com.ovoc01.app.core.annotation.PrimaryKey;
import com.ovoc01.app.core.tools.Utils;
import java.lang.reflect.Field;

/**
 * Subclass of DtbObject with additional functionality to build a primary key.
 */
public class DtbObjectAccess extends DtbObject {
    /**
     * Builds a primary key with a specified length.
     *
     * @param connection The database connection to use for generating the key.
     * @param length     The desired length of the generated key.
     * @throws Exception If an error occurs during key generation or assignment.
     */
    public void buildPk(Connection connection, int length) throws Exception {
        Field pk = getPrimaryKey();
        pk.setAccessible(true);
        pk.set(this, pk.getAnnotation(PrimaryKey.class).prefix()
                + Utils.constructBasicStringSequence(this, connection, length));
    }

    public void buildSequence(Connection c) throws Exception{
        Field pk = getPrimaryKey();
        pk.setAccessible(true);
        pk.set(this,Utils.currentSeqVal(pk.getAnnotation(PrimaryKey.class).sequence(), pk, c));
    }

    public void generateUUID() throws IllegalArgumentException, IllegalAccessException{
         Field pk = getPrimaryKey();
        pk.setAccessible(true);
        pk.set(this,Utils.genereateMostSignificantBitsFor_UUID());
    }
    
}