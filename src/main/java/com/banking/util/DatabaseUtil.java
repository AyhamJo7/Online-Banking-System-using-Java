package com.banking.util;

import java.sql.*;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database utility class for managing database connections.
 * This class implements the Singleton pattern for centralized database access.
 */
public class DatabaseUtil {
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());
    private static DatabaseUtil instance;
    private String url;
    private String username;
    private String password;
    private String driverClass;
    
    /**
     * Private constructor to prevent direct instantiation.
     */
    private DatabaseUtil() {
        try {
            // Ideally, these would be loaded from a properties file
            driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            url = "jdbc:sqlserver://127.0.0.1:1433;databaseName=JavaClass;integratedSecurity=true;";
            
            // Load the JDBC driver
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Failed to load database driver", e);
            throw new RuntimeException("Failed to load database driver", e);
        }
    }
    
    /**
     * Gets the singleton instance of the DatabaseUtil.
     * 
     * @return the singleton instance
     */
    public static synchronized DatabaseUtil getInstance() {
        if (instance == null) {
            instance = new DatabaseUtil();
        }
        return instance;
    }
    
    /**
     * Opens a database connection.
     * 
     * @return a Connection object representing the connection to the database
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
    
    /**
     * Closes a database connection.
     * 
     * @param connection the Connection object to close
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }
    
    /**
     * Closes database resources.
     * 
     * @param connection the Connection object to close
     * @param statement the Statement object to close
     * @param resultSet the ResultSet object to close
     */
    public void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing result set", e);
            }
        }
        
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Error closing statement", e);
            }
        }
        
        closeConnection(connection);
    }
    
    /**
     * Executes a query with proper resource handling.
     * 
     * @param sql the SQL query to execute
     * @param processor a functional interface to process the ResultSet
     * @param <T> the return type
     * @return the result of processing the query
     * @throws SQLException if a database access error occurs
     */
    public <T> T executeQuery(String sql, ResultSetProcessor<T> processor) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            return processor.process(resultSet);
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Executes an update with proper resource handling.
     * 
     * @param sql the SQL update statement to execute
     * @return the row count for SQL Data Manipulation Language statements
     * @throws SQLException if a database access error occurs
     */
    public int executeUpdate(String sql) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } finally {
            closeResources(connection, statement, null);
        }
    }
    
    /**
     * Functional interface for processing a ResultSet.
     * 
     * @param <T> the return type of the processing operation
     */
    @FunctionalInterface
    public interface ResultSetProcessor<T> {
        T process(ResultSet resultSet) throws SQLException;
    }
}
