package com.ovoc01.funkyfrog.core.mapping;

import java.sql.Connection;

import com.ovoc01.funkyfrog.core.annotation.PrimaryKey;
import com.ovoc01.funkyfrog.core.tools.BackPack;

import java.lang.reflect.Field;

/**
 * Subclass of DtbObject with additional functionality to build a primary key.
 */
public class FunkyFrogPersist extends FunkyFrogDAO {
    //private transient Field primaryKey;
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
                + BackPack.constructBasicStringSequence(this, connection, length));
    }


    public void buildSequence(Connection c) throws Exception{
        Field pk = getPrimaryKey();
        pk.setAccessible(true);
        pk.set(this,BackPack.currentSeqVal(pk.getAnnotation(PrimaryKey.class).sequence(), pk, c));
    }

    public void generateUUID() throws IllegalArgumentException, IllegalAccessException{
         Field pk = getPrimaryKey();
        pk.setAccessible(true);
        pk.set(this,BackPack.genereateMostSignificantBitsFor_UUID());
    }
    
}