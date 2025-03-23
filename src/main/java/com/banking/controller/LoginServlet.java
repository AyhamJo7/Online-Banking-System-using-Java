package com.banking.controller;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.service.AccountService;

/**
 * Servlet for handling user login.
 */
public class LoginServlet {
    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());
    
    private String username;
    private String password;
    private PrintWriter output;
    private AccountService accountService;

    /**
     * Constructor for LoginServlet.
     */
    public LoginServlet() {
        username = "";
        password = "";
        accountService = new AccountService();
    }

    /**
     * Authenticates a user login.
     * 
     * @param username the username
     * @param password the password
     * @return the user's name if authentication was successful, empty string otherwise
     */
    public String authenticateUser(String username, String password) {
        this.username = username;
        this.password = password;
        
        LOGGER.info("Login attempt for user: " + username);
        
        try {
            String customerName = accountService.authenticateUser(username, password);
            
            if (customerName != null && !customerName.isEmpty()) {
                // Authentication successful
                LOGGER.info("Authentication successful for user: " + username);
                return customerName;
            } else {
                // Authentication failed
                LOGGER.info("Authentication failed for user: " + username);
                return "";
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during login process", e);
            return "";
        }
    }
    
    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
