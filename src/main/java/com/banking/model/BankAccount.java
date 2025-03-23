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
 * Abstract base class for all bank account types.
 */
public abstract class BankAccount {
    private static final Logger LOGGER = Logger.getLogger(BankAccount.class.getName());
    
    protected String accountNumber;
    protected String customerName;
    protected String customerId;
    protected BigDecimal balance;
    
    /**
     * Constructor for a bank account.
     * 
     * @param accountNumber the account number
     * @param customerName the customer name
     * @param customerId the customer ID
     * @param balance the initial balance
     */
    public BankAccount(String accountNumber, String customerName, String customerId, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.customerName = customerName;
        this.customerId = customerId;
        this.balance = balance;
    }
    
    /**
     * Constructor with account number only.
     * 
     * @param accountNumber the account number
     */
    public BankAccount(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    /**
     * Default constructor.
     */
    public BankAccount() {
    }
    
    /**
     * Gets the account's current balance.
     * 
     * @return the current balance
     */
    public abstract BigDecimal getBalance();
    
    /**
     * Gets the balance for a specific account number.
     * 
     * @param accountNumber the account number
     * @return the current balance
     */
    public abstract BigDecimal getBalance(String accountNumber);
    
    /**
     * Deposits funds into the account.
     * 
     * @param amount the amount to deposit
     * @param customerId the customer ID making the deposit
     * @return true if the deposit was successful, false otherwise
     */
    public abstract boolean deposit(BigDecimal amount, String customerId);
    
    /**
     * Withdraws funds from the account.
     * 
     * @param amount the amount to withdraw
     * @param customerId the customer ID making the withdrawal
     * @return true if the withdrawal was successful, false otherwise
     */
    public abstract boolean withdraw(BigDecimal amount, String customerId);
    
    /**
     * Opens a new account.
     * 
     * @return true if account creation was successful, false otherwise
     */
    public abstract boolean openAccount();
    
    /**
     * Gets the account number associated with a customer ID.
     * 
     * @param customerId the customer ID
     * @return the account number
     */
    public abstract String getAccountNumber(String customerId);
    
    /**
     * Validates that an amount is positive.
     * 
     * @param amount the amount to validate
     * @return true if the amount is valid, false otherwise
     */
    protected boolean validateAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Validates that there are sufficient funds for a withdrawal.
     * 
     * @param withdrawalAmount the amount to be withdrawn
     * @return true if there are sufficient funds, false otherwise
     */
    protected boolean validateSufficientFunds(BigDecimal withdrawalAmount) {
        return balance != null && balance.compareTo(withdrawalAmount) >= 0;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
