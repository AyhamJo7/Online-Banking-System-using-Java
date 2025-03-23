package com.banking.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.util.DatabaseUtil;

/**
 * Model class representing a savings account in the banking system.
 */
public class SavingsAccount extends BankAccount {
    private static final Logger LOGGER = Logger.getLogger(SavingsAccount.class.getName());
    private static final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("0.02"); // 2% interest rate
    
    private BigDecimal interestRate;
    
    /**
     * Constructor for a savings account.
     * 
     * @param accountNumber the account number
     * @param customerName the customer name
     * @param customerId the customer ID
     * @param initialDeposit the initial deposit amount
     */
    public SavingsAccount(String accountNumber, String customerName, String customerId, String initialDeposit) {
        super(accountNumber, customerName, customerId, 
              initialDeposit != null ? new BigDecimal(initialDeposit) : BigDecimal.ZERO);
        this.interestRate = DEFAULT_INTEREST_RATE;
    }
    
    /**
     * Constructor with account number only.
     * 
     * @param accountNumber the account number
     */
    public SavingsAccount(String accountNumber) {
        super(accountNumber);
    }
    
    /**
     * Default constructor.
     */
    public SavingsAccount() {
        super();
    }
    
    /**
     * Opens a new savings account.
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
            String checkAccountSql = "SELECT SavingAccountNumber FROM SavingAccount WHERE SavingAccountNumber = '" 
                                    + accountNumber + "'";
            resultSet = statement.executeQuery(checkAccountSql);
            
            if (resultSet.next()) {
                LOGGER.warning("Savings account number already exists: " + accountNumber);
                return false;
            }
            
            // Insert new savings account
            String insertAccountSql = "INSERT INTO SavingAccount(SavingAccountNumber, CustomerName, Balance, CustomerID) VALUES ('"
                    + accountNumber + "','" + customerName + "'," + balance + ",'" + customerId + "')";
            statement.executeUpdate(insertAccountSql);
            
            LOGGER.info("Savings account created successfully: " + accountNumber);
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating savings account", e);
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
            
            String sql = "SELECT SavingAccountNumber FROM SavingAccount WHERE CustomerID = '" + customerId + "'";
            resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()) {
                accountNumber = resultSet.getString("SavingAccountNumber");
                return accountNumber;
            }
            
            return null;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving savings account number", e);
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
            
            String sql = "SELECT Balance FROM SavingAccount WHERE SavingAccountNumber = '" + accountNumber + "'";
            resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()) {
                balance = BigDecimal.valueOf(resultSet.getFloat(1));
                return balance;
            }
            
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving savings account balance", e);
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
            
            String sql = "SELECT Balance FROM SavingAccount WHERE SavingAccountNumber = '" + accountNumber + "'";
            resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()) {
                return BigDecimal.valueOf(resultSet.getFloat(1));
            }
            
            return BigDecimal.ZERO;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving savings account balance", e);
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
            String getBalanceSql = "SELECT Balance FROM SavingAccount WHERE SavingAccountNumber = '" 
                                  + accountNumber + "' AND CustomerID = '" + customerId + "'";
            resultSet = statement.executeQuery(getBalanceSql);
            
            if (resultSet.next()) {
                balance = BigDecimal.valueOf(resultSet.getFloat(1));
                balance = balance.add(amount);
                
                // Update balance
                String updateBalanceSql = "UPDATE SavingAccount SET Balance = " + balance 
                                         + " WHERE SavingAccountNumber = '" + accountNumber + "'";
                statement.executeUpdate(updateBalanceSql);
                
                LOGGER.info("Deposit successful to savings account: " + accountNumber);
                return true;
            } else {
                LOGGER.warning("Account not found for deposit: " + accountNumber);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing deposit to savings account", e);
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
            String getBalanceSql = "SELECT Balance FROM SavingAccount WHERE SavingAccountNumber = '" 
                                  + accountNumber + "' AND CustomerID = '" + customerId + "'";
            resultSet = statement.executeQuery(getBalanceSql);
            
            if (resultSet.next()) {
                balance = BigDecimal.valueOf(resultSet.getFloat(1));
                
                if (!validateSufficientFunds(amount)) {
                    LOGGER.warning("Insufficient funds for withdrawal from savings account: " + accountNumber);
                    return false;
                }
                
                balance = balance.subtract(amount);
                
                // Update balance
                String updateBalanceSql = "UPDATE SavingAccount SET Balance = " + balance 
                                         + " WHERE SavingAccountNumber = '" + accountNumber + "'";
                statement.executeUpdate(updateBalanceSql);
                
                LOGGER.info("Withdrawal successful from savings account: " + accountNumber);
                return true;
            } else {
                LOGGER.warning("Account not found for withdrawal: " + accountNumber);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error processing withdrawal from savings account", e);
            return false;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Calculates the interest for the savings account.
     * 
     * @return the interest amount
     */
    public BigDecimal calculateInterest() {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal currentBalance = getBalance();
        if (currentBalance.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        
        // Simple interest calculation
        BigDecimal interest = currentBalance.multiply(interestRate)
                .setScale(2, RoundingMode.HALF_EVEN);
        
        LOGGER.info("Interest calculated for savings account " + accountNumber + ": " + interest);
        return interest;
    }
    
    /**
     * Applies the calculated interest to the account balance.
     * 
     * @return true if interest was applied successfully, false otherwise
     */
    public boolean applyInterest() {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return false;
        }
        
        BigDecimal interestAmount = calculateInterest();
        
        if (interestAmount.compareTo(BigDecimal.ZERO) <= 0) {
            LOGGER.info("No interest to apply for account: " + accountNumber);
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
            String getBalanceSql = "SELECT Balance FROM SavingAccount WHERE SavingAccountNumber = '" 
                                  + accountNumber + "'";
            resultSet = statement.executeQuery(getBalanceSql);
            
            if (resultSet.next()) {
                balance = BigDecimal.valueOf(resultSet.getFloat(1));
                balance = balance.add(interestAmount);
                
                // Update balance
                String updateBalanceSql = "UPDATE SavingAccount SET Balance = " + balance 
                                         + " WHERE SavingAccountNumber = '" + accountNumber + "'";
                statement.executeUpdate(updateBalanceSql);
                
                LOGGER.info("Interest applied to savings account: " + accountNumber);
                return true;
            } else {
                LOGGER.warning("Account not found for applying interest: " + accountNumber);
                return false;
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error applying interest to savings account", e);
            return false;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Gets the interest rate for the account.
     * 
     * @return the interest rate as a BigDecimal
     */
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    
    /**
     * Sets the interest rate for the account.
     * 
     * @param interestRate the interest rate to set
     */
    public void setInterestRate(BigDecimal interestRate) {
        if (interestRate != null && interestRate.compareTo(BigDecimal.ZERO) >= 0) {
            this.interestRate = interestRate;
        }
    }
}
