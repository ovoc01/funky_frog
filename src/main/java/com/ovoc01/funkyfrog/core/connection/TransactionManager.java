package com.ovoc01.funkyfrog.core.connection;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface TransactionManager {
    void execute(Connection c);

public class MyTransaction {
    private Connection connection;

    public MyTransaction(Connection connection) {
        this.connection = connection;
    }

    public void executeTransactionalCode(TransactionManager transactionCode)throws Exception {
        try {
            
            connection.setAutoCommit(false);

            
            transactionCode.execute(connection);

            
            connection.commit();
        } catch (SQLException e) {
            
            rollbackTransaction(connection);
            e.printStackTrace();
        } finally {
            
            setAutoCommit(connection, true);
        }
    }

    private void rollbackTransaction(Connection connection) {
        try {
            
            connection.rollback();
        } catch (SQLException rollbackException) {
            rollbackException.printStackTrace();
        }
    }

    private void setAutoCommit(Connection connection, boolean autoCommit) {
        try {
            
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
} 
