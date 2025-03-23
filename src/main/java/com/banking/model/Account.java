package com.banking.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.util.DatabaseUtil;

/**
 * Model class representing a user account in the banking system.
 */
public class Account {
    private static final Logger LOGGER = Logger.getLogger(Account.class.getName());
    
    private String username;
    private String password;
    private String confirmedPassword;
    private String name;

    /**
     * Constructor for sign-up process.
     * 
     * @param username the username
     * @param password the password
     * @param confirmedPassword the confirmed password
     * @param name the user's full name
     */
    public Account(String username, String password, String confirmedPassword, String name) {
        this.username = username;
        this.password = password;
        this.confirmedPassword = confirmedPassword;
        this.name = name;
    }

    /**
     * Constructor for sign-in process.
     * 
     * @param username the username
     * @param password the password
     */
    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Registers a new user account.
     * 
     * @return true if registration was successful, false otherwise
     */
    public boolean signUp() {
        if (!validateSignUpInput()) {
            return false;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            // Check if username already exists
            String checkUserSql = "SELECT Username FROM Account WHERE Username = '" + username + "'";
            resultSet = statement.executeQuery(checkUserSql);
            
            if (resultSet.next()) {
                LOGGER.info("Username already exists: " + username);
                return false;
            }
            
            // Insert new account
            String insertAccountSql = "INSERT INTO Account(Username, Password, Name) VALUES ('"
                    + username + "','" + password + "','" + name + "')";
            statement.executeUpdate(insertAccountSql);
            
            LOGGER.info("Account created successfully for: " + username);
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating account", e);
            return false;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }

    /**
     * Authenticates a user login attempt.
     * 
     * @return the user's name if authentication successful, empty string otherwise
     */
    public String signIn() {
        if (username.isEmpty() || password.isEmpty()) {
            return "";
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            String sql = "SELECT Name FROM Account WHERE Username = '" + username
                    + "' AND Password = '" + password + "'";
            resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()) {
                name = resultSet.getString(1);
                LOGGER.info("User authenticated: " + username);
                return name;
            } else {
                LOGGER.info("Authentication failed for: " + username);
                return "";
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error during authentication", e);
            return "";
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }

    /**
     * Changes a user's password.
     * 
     * @param newPassword the new password to set
     * @return true if password was changed successfully, false otherwise
     */
    public boolean changePassword(String newPassword) {
        if (username.isEmpty() || password.isEmpty() || newPassword.isEmpty()) {
            return false;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            // Verify current credentials
            String checkUserSql = "SELECT * FROM Account WHERE Username = '" + username
                    + "' AND Password = '" + password + "'";
            resultSet = statement.executeQuery(checkUserSql);
            
            if (!resultSet.next()) {
                LOGGER.info("Current credentials are invalid for user: " + username);
                return false;
            }
            
            // Update password
            String updatePasswordSql = "UPDATE Account SET Password = '" + newPassword
                    + "' WHERE Username = '" + username + "'";
            statement.executeUpdate(updatePasswordSql);
            
            LOGGER.info("Password updated successfully for user: " + username);
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error changing password", e);
            return false;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Validates user input for sign-up.
     * 
     * @return true if input is valid, false otherwise
     */
    private boolean validateSignUpInput() {
        return !username.isEmpty() && !password.isEmpty() 
               && !confirmedPassword.isEmpty() && password.equals(confirmedPassword);
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }
}
