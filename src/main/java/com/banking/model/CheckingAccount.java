package com.banking.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.util.DatabaseUtil;

/**
 * Model class representing a checking account in the banking system.
 */
public class CheckingAccount extends BankAccount {
    private static final Logger LOGGER = Logger.getLogger(CheckingAccount.class.getName());
    
    /**
     * Constructor for a checking account.
     * 
     * @param accountNumber the account number
     * @param customerName the customer name
     * @param customerId the customer ID
     * @param initialDeposit the initial deposit amount
     */
    public CheckingAccount(String accountNumber, String customerName, String customerId, String initialDeposit) {
        super(accountNumber, customerName, customerId, 
              initialDeposit != null ? new BigDecimal(initialDeposit) : BigDecimal.ZERO);
    }
    
    /**
     * Constructor with account number only.
     * 
     * @param accountNumber the account number
     */
    public CheckingAccount(String accountNumber) {
        super(accountNumber);
    }
    
    /**
     * Default constructor.
     */
    public CheckingAccount() {
        super();
    }
    
    /**
     * Opens a new checking account.
     * 
     * @return true if account creation was successful, false otherwise
     */
    @Override
    public boolean openAccount() {
        if (accountNumber == null || accountNumber.isEmpty() || 
            customerName == null || customerName.isEmpty() || 
            customerId == null || customerId.isEmpty() || 
            balance == null) {
            LOGGER.warning("Invalid account information provided");
            return false;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            // Check if account number already exists
            String checkAccountSql = "SELECT CheckingAccountNumber FROM CheckingAccount WHERE CheckingAccountNumber = '" 
                                    + accountNumber + "'";
            resultSet = statement.executeQuery(checkAccountSql);
            
            if (resultSet.next()) {
                LOGGER.warning("Checking account number already exists: " + accountNumber);
                return false;
            }
            
            // Insert new checking account
            String insertAccountSql = "INSERT INTO CheckingAccount(CheckingAccountNumber, CustomerName, Balance, CustomerID) VALUES ('"
                    + accountNumber + "','" + customerName + "'," + balance + ",'" + customerId + "')";
            statement.executeUpdate(insertAccountSql);
            
            LOGGER.info("Checking account created successfully: " + accountNumber);
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating checking account", e);
            return false;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Gets the account number associated with a customer ID.
     * 
     * @param customerId the customer ID
     * @return the account number
     */
    @Override
    public String getAccountNumber(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            return null;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            String sql = "SELECT CheckingAccountNumber FROM CheckingAccount WHERE CustomerID = '" + customerId + "'";
            resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()) {
                accountNumber = resultSet.getString("CheckingAccountNumber");
                return accountNumber;
            }
            
            return null;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving checking account number", e);
            return null;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Gets the account's current balance.
     * 
     * @return the current balance
     */
    @Override
    public BigDecimal getBalance() {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            String sql = "SELECT Balance FROM CheckingAccount WHERE CheckingAccountNumber = '" + accountNumber + "'";
            resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()) {
                balance = BigDecimal.valueOf(resultSet.getFloat(1));
                return balance;
            }
            
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving checking account balance", e);
            return BigDecimal.ZERO;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Gets the balance for a specific account number.
     * 
     * @param accountNumber the account number
     * @return the current balance
     */
    @Override
    public BigDecimal getBalance(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            String sql = "SELECT Balance FROM CheckingAccount WHERE CheckingAccountNumber = '" + accountNumber + "'";
            resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()) {
                return BigDecimal.valueOf(resultSet.getFloat(1));
            }
            
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving checking account balance", e);
            return BigDecimal.ZERO;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Deposits funds into the account.
     * 
     * @param amount the amount to deposit
     * @param customerId the customer ID making the deposit
     * @return true if the deposit was successful, false otherwise
     */
    @Override
    public boolean deposit(BigDecimal amount, String customerId) {
        if (accountNumber == null || accountNumber.isEmpty() || customerId == null || customerId.isEmpty()) {
            return false;
        }
        
        if (!validateAmount(amount)) {
            LOGGER.warning("Invalid deposit amount: " + amount);
            return false;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            // Get current balance
            String getBalanceSql = "SELECT Balance FROM CheckingAccount WHERE CheckingAccountNumber = '" 
                                  + accountNumber + "' AND CustomerID = '" + customerId + "'";
            resultSet = statement.executeQuery(getBalanceSql);
            
            if (resultSet.next()) {
                balance = BigDecimal.valueOf(resultSet.getFloat(1));
                balance = balance.add(amount);
                
                // Update balance
                String updateBalanceSql = "UPDATE CheckingAccount SET Balance = " + balance 
                                         + " WHERE CheckingAccountNumber = '" + accountNumber + "'";
                statement.executeUpdate(updateBalanceSql);
                
                LOGGER.info("Deposit successful to checking account: " + accountNumber);
                return true;
            } else {
                LOGGER.warning("Account not found for deposit: " + accountNumber);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing deposit to checking account", e);
            return false;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Withdraws funds from the account.
     * 
     * @param amount the amount to withdraw
     * @param customerId the customer ID making the withdrawal
     * @return true if the withdrawal was successful, false otherwise
     */
    @Override
    public boolean withdraw(BigDecimal amount, String customerId) {
        if (accountNumber == null || accountNumber.isEmpty() || customerId == null || customerId.isEmpty()) {
            return false;
        }
        
        if (!validateAmount(amount)) {
            LOGGER.warning("Invalid withdrawal amount: " + amount);
            return false;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            // Get current balance
            String getBalanceSql = "SELECT Balance FROM CheckingAccount WHERE CheckingAccountNumber = '" 
                                  + accountNumber + "' AND CustomerID = '" + customerId + "'";
            resultSet = statement.executeQuery(getBalanceSql);
            
            if (resultSet.next()) {
                balance = BigDecimal.valueOf(resultSet.getFloat(1));
                
                if (!validateSufficientFunds(amount)) {
                    LOGGER.warning("Insufficient funds for withdrawal from checking account: " + accountNumber);
                    return false;
                }
                
                balance = balance.subtract(amount);
                
                // Update balance
                String updateBalanceSql = "UPDATE CheckingAccount SET Balance = " + balance 
                                         + " WHERE CheckingAccountNumber = '" + accountNumber + "'";
                statement.executeUpdate(updateBalanceSql);
                
                LOGGER.info("Withdrawal successful from checking account: " + accountNumber);
                return true;
            } else {
                LOGGER.warning("Account not found for withdrawal: " + accountNumber);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing withdrawal from checking account", e);
            return false;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
}
