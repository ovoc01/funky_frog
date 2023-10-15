package com.ovoc01.app.core.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import org.w3c.dom.Element;
import com.ovoc01.app.core.connection.reader.XmlReader;
import com.ovoc01.app.core.tools.Utils;
/**
 * This class provides methods to create database connections for PostgreSQL and MySQL databases.
 */
public class DtbConnexion {

    /**
     * Creates a PostgreSQL database connection.
     *
     * @param host the host of the connection
     * @param port the port to be used
     * @param user the username
     * @param pwd the password
     * @param dbname the database name
     * @return a Connection object representing the database connection
     */
    public static Connection initPgCon(String host, String port, String user, String pwd, String dbname) {
        Connection result = null;
        String strConn = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;

        try {
            Class.forName("org.postgresql.Driver");
            result = DriverManager.getConnection(strConn, user, pwd);
            result.setAutoCommit(false);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a PostgreSQL database connection with default port.
     *
     * @param host the host of the connection
     * @param user the username
     * @param pwd the password
     * @param dbname the database name
     * @return a Connection object representing the database connection
     */
    public static Connection initPgCon(String host, String user, String pwd, String dbname) {
        Connection result = null;
        String strConn = "jdbc:postgresql://" + host + "/" + dbname;

        try {
            Class.forName("org.postgresql.Driver");
            result = DriverManager.getConnection(strConn, user, pwd);
            result.setAutoCommit(false);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates a database connection based on the given XML element.
     *
     * @param connection the XML element containing the connection details
     * @return a Connection object representing the database connection
     */
    private static Connection initPgCon(Element connection) {
        String host = XmlReader.getElementValue(connection, "host");
        String port = XmlReader.getElementValue(connection, "port");
        String user = XmlReader.getElementValue(connection, "user");
        String pwd = XmlReader.getElementValue(connection, "pwd");
        String dbname = XmlReader.getElementValue(connection, "dbname");
        return initPgCon(host, port, user, pwd, dbname);
    }

    /**
     * Creates a database connection based on the given XML element.
     *
     * @param connection the XML element containing the connection details
     * @return a Connection object representing the database connection
     */
    public static Connection initCon(Element connection) {
        String server = XmlReader.getElementValue(connection, "server");
        String host = XmlReader.getElementValue(connection, "host");
        String port = XmlReader.getElementValue(connection, "port");
        String user = XmlReader.getElementValue(connection, "user");
        String pwd = XmlReader.getElementValue(connection, "pwd");
        String dbname = XmlReader.getElementValue(connection, "dbname");
        switch (server) {
            case "mysql":
                return initMySqlCon(host, port, user, pwd, dbname);

            case "postgres":
                return initPgCon(connection);

            default:
                return null;
        }
    }

    /**
     * Creates a MySQL database connection.
     *
     * @param host the host of the connection
     * @param port the port to be used
     * @param user the username
     * @param pwd the password
     * @param dbname the database name
     * @return a Connection object representing the database connection
     */
    public static Connection initMySqlCon(String host, String port, String user, String pwd, String dbname) {
        Connection result = null;
        String strConn = "jdbc:mysql://" + host + ":" + port + "/" + dbname;

        try {
            result = DriverManager.getConnection(strConn, user, pwd);
            result.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Creates a database connection based on the details in the "database.xml" file.
     *
     * @return a Connection object representing the database connection
     * @throws Exception if there is an error reading the XML file or creating the database connection
     */
    public static Connection sessionConnection() throws Exception {
        String path = Utils.currentLocation() + "/database.xml";
        Element element = XmlReader.readXml(path);
        return initCon(element);
    }

    /**
     * Creates a database connection based on the details in the "database.xml" file for the specified database name.
     *
     * @param databaseName the name of the database to connect to
     * @return a Connection object representing the database connection
     * @throws Exception if there is an error reading the XML file or creating the database connection
     */
    public static Connection sessionConnection(String databaseName) throws Exception {
        String path = Utils.currentLocation() + "/database.xml";

        Element element = XmlReader.readXml(path);
        Element connection = XmlReader.getElement(element, "connection", databaseName);
        return initCon(connection);
    }
}
