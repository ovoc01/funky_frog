package com.ovoc01.funkyfrog.core.mapping.spec;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypedQuery<U> {

    String query;
    Class<?> returnType;

    public TypedQuery(Class<?> clazz) {
        this.returnType = clazz;
    }

    public TypedQuery<U> createNativeQuery(String query) {
        this.query = query;
        return this;
    }

    public TypedQuery<U> setParamater(String key, Object value) {
        if(value instanceof String) {
            value = "'" + value + "'";
        }
        this.query = query.replaceAll(":" + key, String.valueOf(value));
        return this;
    }

    public U fetchSingleResult(Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            // Assuming U is a type that can be returned from the ResultSet
            return (U) rs.getObject(1);
        } else {
            return null;
        }
    }

    public U[] fetchResults(Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        List<U> results = new ArrayList<>();
        System.out.println(query);
        while (rs.next()) {
            // Assuming U is a type that can be returned from the ResultSet
            results.add((U) rs.getObject(1));
        }
        return results.toArray((U[]) Array.newInstance(returnType, results.size()));
    }

    public TypedQuery<U> reset() {
        this.query = null;
        this.returnType = null;
        return this;
    }
}
