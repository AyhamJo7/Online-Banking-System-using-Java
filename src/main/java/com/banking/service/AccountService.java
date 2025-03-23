package com.banking.service;

import java.math.BigDecimal;
import java.util.logging.Logger;

import com.banking.model.Account;
import com.banking.model.CheckingAccount;
import com.banking.model.SavingsAccount;
import com.banking.model.Transaction;

/**
 * Service class for account operations.
 */
public class AccountService {
    private static final Logger LOGGER = Logger.getLogger(AccountService.class.getName());
    
    /**
     * Creates a new user account.
     * 
     * @param username the username
     * @param password the password
     * @param confirmedPassword the confirmed password
     * @param name the user's full name
     * @return true if the account was created successfully, false otherwise
     */
    public boolean createUserAccount(String username, String password, String confirmedPassword, String name) {
        Account account = new Account(username, password, confirmedPassword, name);
        return account.signUp();
    }
    
    /**
     * Authenticates a user login.
     * 
     * @param username the username
     * @param password the password
     * @return the user's name if authentication was successful, empty string otherwise
     */
    public String authenticateUser(String username, String password) {
        Account account = new Account(username, password);
        return account.signIn();
    }
    
    /**
     * Changes a user's password.
     * 
     * @param username the username
     * @param currentPassword the current password
     * @param newPassword the new password
     * @return true if the password was changed successfully, false otherwise
     */
    public boolean changePassword(String username, String currentPassword, String newPassword) {
        Account account = new Account(username, currentPassword);
        return account.changePassword(newPassword);
    }
    
    /**
     * Opens a new checking account.
     * 
     * @param accountNumber the account number
     * @param customerName the customer name
     * @param customerId the customer ID
     * @param initialDeposit the initial deposit amount
     * @return true if the account was created successfully, false otherwise
     */
    public boolean openCheckingAccount(String accountNumber, String customerName, String customerId, String initialDeposit) {
        CheckingAccount account = new CheckingAccount(accountNumber, customerName, customerId, initialDeposit);
        boolean success = account.openAccount();
        
        if (success) {
            // Record the transaction
            Transaction transaction = new Transaction(accountNumber, customerId, initialDeposit);
            transaction.setTransactionType("Opening Deposit");
            transaction.recordTransaction();
        }
        
        return success;
    }
    
    /**
     * Opens a new savings account.
     * 
     * @param accountNumber the account number
     * @param customerName the customer name
     * @param customerId the customer ID
     * @param initialDeposit the initial deposit amount
     * @return true if the account was created successfully, false otherwise
     */
    public boolean openSavingsAccount(String accountNumber, String customerName, String customerId, String initialDeposit) {
        SavingsAccount account = new SavingsAccount(accountNumber, customerName, customerId, initialDeposit);
        boolean success = account.openAccount();
        
        if (success) {
            // Record the transaction
            Transaction transaction = new Transaction(accountNumber, customerId, initialDeposit);
            transaction.setTransactionType("Opening Deposit");
            transaction.recordTransaction();
        }
        
        return success;
    }
    
    /**
     * Gets the balance of a checking account.
     * 
     * @param accountNumber the account number
     * @return the account balance
     */
    public BigDecimal getCheckingBalance(String accountNumber) {
        CheckingAccount account = new CheckingAccount(accountNumber);
        return account.getBalance();
    }
    
    /**
     * Gets the balance of a savings account.
     * 
     * @param accountNumber the account number
     * @return the account balance
     */
    public BigDecimal getSavingsBalance(String accountNumber) {
        SavingsAccount account = new SavingsAccount(accountNumber);
        return account.getBalance();
    }
    
    /**
     * Gets the checking account number for a customer.
     * 
     * @param customerId the customer ID
     * @return the account number
     */
    public String getCheckingAccountNumber(String customerId) {
        CheckingAccount account = new CheckingAccount();
        return account.getAccountNumber(customerId);
    }
    
    /**
     * Gets the savings account number for a customer.
     * 
     * @param customerId the customer ID
     * @return the account number
     */
    public String getSavingsAccountNumber(String customerId) {
        SavingsAccount account = new SavingsAccount();
        return account.getAccountNumber(customerId);
    }
}
