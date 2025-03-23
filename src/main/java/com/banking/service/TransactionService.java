package com.banking.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import com.banking.model.CheckingAccount;
import com.banking.model.SavingsAccount;
import com.banking.model.Transaction;
import com.banking.model.Transaction.TransactionDetails;

/**
 * Service class for transaction operations.
 */
public class TransactionService {
    private static final Logger LOGGER = Logger.getLogger(TransactionService.class.getName());
    
    /**
     * Deposits funds into a checking account.
     * 
     * @param accountNumber the account number
     * @param customerId the customer ID
     * @param amount the amount to deposit
     * @return true if the deposit was successful, false otherwise
     */
    public boolean depositToChecking(String accountNumber, String customerId, String amount) {
        CheckingAccount account = new CheckingAccount(accountNumber);
        boolean success = account.deposit(new BigDecimal(amount), customerId);
        
        if (success) {
            // Record the transaction
            Transaction transaction = new Transaction(accountNumber, customerId, amount);
            transaction.setTransactionType("Deposit");
            transaction.recordTransaction();
        }
        
        return success;
    }
    
    /**
     * Deposits funds into a savings account.
     * 
     * @param accountNumber the account number
     * @param customerId the customer ID
     * @param amount the amount to deposit
     * @return true if the deposit was successful, false otherwise
     */
    public boolean depositToSavings(String accountNumber, String customerId, String amount) {
        SavingsAccount account = new SavingsAccount(accountNumber);
        boolean success = account.deposit(new BigDecimal(amount), customerId);
        
        if (success) {
            // Record the transaction
            Transaction transaction = new Transaction(accountNumber, customerId, amount);
            transaction.setTransactionType("Deposit");
            transaction.recordTransaction();
        }
        
        return success;
    }
    
    /**
     * Withdraws funds from a checking account.
     * 
     * @param accountNumber the account number
     * @param customerId the customer ID
     * @param amount the amount to withdraw
     * @return true if the withdrawal was successful, false otherwise
     */
    public boolean withdrawFromChecking(String accountNumber, String customerId, String amount) {
        CheckingAccount account = new CheckingAccount(accountNumber);
        boolean success = account.withdraw(new BigDecimal(amount), customerId);
        
        if (success) {
            // Record the transaction
            Transaction transaction = new Transaction(null, accountNumber, customerId, amount, "Withdrawal");
            transaction.recordTransaction();
        }
        
        return success;
    }
    
    /**
     * Withdraws funds from a savings account.
     * 
     * @param accountNumber the account number
     * @param customerId the customer ID
     * @param amount the amount to withdraw
     * @return true if the withdrawal was successful, false otherwise
     */
    public boolean withdrawFromSavings(String accountNumber, String customerId, String amount) {
        SavingsAccount account = new SavingsAccount(accountNumber);
        boolean success = account.withdraw(new BigDecimal(amount), customerId);
        
        if (success) {
            // Record the transaction
            Transaction transaction = new Transaction(null, accountNumber, customerId, amount, "Withdrawal");
            transaction.recordTransaction();
        }
        
        return success;
    }
    
    /**
     * Transfers funds between accounts.
     * 
     * @param fromAccountNumber the source account number
     * @param toAccountNumber the destination account number
     * @param customerId the customer ID
     * @param amount the amount to transfer
     * @param fromAccountType the source account type (checking or savings)
     * @param toAccountType the destination account type (checking or savings)
     * @return true if the transfer was successful, false otherwise
     */
    public boolean transfer(String fromAccountNumber, String toAccountNumber, String customerId, 
                          String amount, String fromAccountType, String toAccountType) {
        
        boolean withdrawSuccess = false;
        
        // Withdraw from the source account
        if ("checking".equalsIgnoreCase(fromAccountType)) {
            CheckingAccount fromAccount = new CheckingAccount(fromAccountNumber);
            withdrawSuccess = fromAccount.withdraw(new BigDecimal(amount), customerId);
        } else if ("savings".equalsIgnoreCase(fromAccountType)) {
            SavingsAccount fromAccount = new SavingsAccount(fromAccountNumber);
            withdrawSuccess = fromAccount.withdraw(new BigDecimal(amount), customerId);
        } else {
            LOGGER.warning("Invalid source account type: " + fromAccountType);
            return false;
        }
        
        if (!withdrawSuccess) {
            LOGGER.warning("Transfer failed during withdrawal from " + fromAccountType + " account: " + fromAccountNumber);
            return false;
        }
        
        // Deposit to the destination account
        boolean depositSuccess = false;
        
        if ("checking".equalsIgnoreCase(toAccountType)) {
            CheckingAccount toAccount = new CheckingAccount(toAccountNumber);
            depositSuccess = toAccount.deposit(new BigDecimal(amount), customerId);
        } else if ("savings".equalsIgnoreCase(toAccountType)) {
            SavingsAccount toAccount = new SavingsAccount(toAccountNumber);
            depositSuccess = toAccount.deposit(new BigDecimal(amount), customerId);
        } else {
            LOGGER.warning("Invalid destination account type: " + toAccountType);
            
            // If deposit fails, revert the withdrawal
            if ("checking".equalsIgnoreCase(fromAccountType)) {
                CheckingAccount fromAccount = new CheckingAccount(fromAccountNumber);
                fromAccount.deposit(new BigDecimal(amount), customerId);
            } else if ("savings".equalsIgnoreCase(fromAccountType)) {
                SavingsAccount fromAccount = new SavingsAccount(fromAccountNumber);
                fromAccount.deposit(new BigDecimal(amount), customerId);
            }
            
            return false;
        }
        
        if (!depositSuccess) {
            LOGGER.warning("Transfer failed during deposit to " + toAccountType + " account: " + toAccountNumber);
            
            // If deposit fails, revert the withdrawal
            if ("checking".equalsIgnoreCase(fromAccountType)) {
                CheckingAccount fromAccount = new CheckingAccount(fromAccountNumber);
                fromAccount.deposit(new BigDecimal(amount), customerId);
            } else if ("savings".equalsIgnoreCase(fromAccountType)) {
                SavingsAccount fromAccount = new SavingsAccount(fromAccountNumber);
                fromAccount.deposit(new BigDecimal(amount), customerId);
            }
            
            return false;
        }
        
        // Record the transaction
        Transaction transaction = new Transaction(toAccountNumber, fromAccountNumber, customerId, amount, "Transfer");
        transaction.recordTransaction();
        
        return true;
    }
    
    /**
     * Searches for transactions within a date range.
     * 
     * @param customerId the customer ID
     * @param startDate the start date (yyyy-MM-dd)
     * @param endDate the end date (yyyy-MM-dd)
     * @return a list of matching transactions
     */
    public List<TransactionDetails> searchTransactions(String customerId, String startDate, String endDate) {
        Transaction transaction = new Transaction(startDate, endDate);
        return transaction.searchTransactions(customerId);
    }
}
