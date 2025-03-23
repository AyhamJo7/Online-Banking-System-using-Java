package com.banking.model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.banking.util.DatabaseUtil;

/**
 * Model class representing a banking transaction.
 */
public class Transaction {
    private static final Logger LOGGER = Logger.getLogger(Transaction.class.getName());
    
    private String transactionNumber;
    private String transactionType;
    private String transactionTime;
    private String transactionDate;
    private String fromAccount;
    private String toAccount;
    private String customerId;
    private BigDecimal amount;
    private String startDate;
    private String endDate;
    
    /**
     * Constructor for deposit transaction.
     * 
     * @param accountType the account type (checking or savings)
     * @param customerId the customer ID
     * @param amount the transaction amount
     */
    public Transaction(String accountType, String customerId, String amount) {
        this.toAccount = accountType;
        this.customerId = customerId;
        this.amount = new BigDecimal(amount);
        this.transactionType = "Deposit";
    }
    
    /**
     * Constructor for transfer or withdrawal transaction.
     * 
     * @param toAccount the destination account
     * @param fromAccount the source account
     * @param customerId the customer ID
     * @param amount the transaction amount
     * @param transactionType the transaction type
     */
    public Transaction(String toAccount, String fromAccount, String customerId, String amount, String transactionType) {
        this.toAccount = toAccount;
        this.fromAccount = fromAccount;
        this.customerId = customerId;
        this.transactionType = transactionType;
        this.amount = new BigDecimal(amount);
    }
    
    /**
     * Constructor for transaction search.
     * 
     * @param startDate the start date for the search range
     * @param endDate the end date for the search range
     */
    public Transaction(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    /**
     * Records a transaction in the database.
     * 
     * @return the transaction number
     */
    public String recordTransaction() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            // Generate a unique transaction number
            boolean isDuplicate = true;
            while (isDuplicate) {
                Random rand = new Random();
                int randomNum = rand.nextInt(9999) + 1000;
                transactionNumber = Integer.toString(randomNum);
                
                String checkTransactionSql = "SELECT TransactionNumber FROM Transactions WHERE TransactionNumber = '" 
                                           + transactionNumber + "'";
                resultSet = statement.executeQuery(checkTransactionSql);
                isDuplicate = resultSet.next();
            }
            
            // Set the transaction time and date
            LocalTime now = LocalTime.now();
            LocalDate today = LocalDate.now();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            transactionTime = now.format(timeFormatter);
            transactionDate = today.format(dateFormatter);
            
            // Insert the transaction
            String insertTransactionSql = "INSERT INTO Transactions(TransactionNumber, TransactionType, TransactionAmount, " +
                                         "TransactionTime, TransactionDate, FromAccount, ToAccount, CustomerID) VALUES ('" +
                                         transactionNumber + "','" + transactionType + "','" + amount + "','" + 
                                         transactionTime + "','" + transactionDate + "','" + fromAccount + "','" + 
                                         toAccount + "','" + customerId + "')";
            
            statement.executeUpdate(insertTransactionSql);
            LOGGER.info("Transaction recorded successfully: " + transactionNumber);
            
            return transactionNumber;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error recording transaction", e);
            return "";
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Searches for transactions within a date range.
     * 
     * @param customerId the customer ID
     * @return a list of matching transactions
     */
    public List<TransactionDetails> searchTransactions(String customerId) {
        List<TransactionDetails> transactions = new ArrayList<>();
        
        if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
            LOGGER.warning("Invalid date range for transaction search");
            return transactions;
        }
        
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        
        try {
            DatabaseUtil dbUtil = DatabaseUtil.getInstance();
            connection = dbUtil.getConnection();
            statement = connection.createStatement();
            
            String searchTransactionSql = "SELECT * FROM Transactions WHERE CustomerID = '" + customerId +
                                          "' AND TransactionDate BETWEEN '" + startDate + "' AND '" + endDate + 
                                          "' ORDER BY TransactionDate DESC, TransactionTime DESC";
            
            resultSet = statement.executeQuery(searchTransactionSql);
            
            while (resultSet.next()) {
                TransactionDetails transaction = new TransactionDetails(
                    resultSet.getString("TransactionNumber"),
                    resultSet.getString("TransactionType"),
                    new BigDecimal(resultSet.getString("TransactionAmount")),
                    resultSet.getString("TransactionTime"),
                    resultSet.getString("TransactionDate"),
                    resultSet.getString("FromAccount"),
                    resultSet.getString("ToAccount")
                );
                
                transactions.add(transaction);
            }
            
            LOGGER.info("Found " + transactions.size() + " transactions for user " + customerId + 
                       " between " + startDate + " and " + endDate);
            
            return transactions;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching for transactions", e);
            return transactions;
        } finally {
            DatabaseUtil.getInstance().closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Inner class to store transaction details.
     */
    public static class TransactionDetails {
        private String transactionNumber;
        private String transactionType;
        private BigDecimal amount;
        private String time;
        private String date;
        private String fromAccount;
        private String toAccount;
        
        /**
         * Constructor for transaction details.
         * 
         * @param transactionNumber the transaction number
         * @param transactionType the transaction type
         * @param amount the transaction amount
         * @param time the transaction time
         * @param date the transaction date
         * @param fromAccount the source account
         * @param toAccount the destination account
         */
        public TransactionDetails(String transactionNumber, String transactionType, BigDecimal amount,
                                 String time, String date, String fromAccount, String toAccount) {
            this.transactionNumber = transactionNumber;
            this.transactionType = transactionType;
            this.amount = amount;
            this.time = time;
            this.date = date;
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
        }

        public String getTransactionNumber() {
            return transactionNumber;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getTime() {
            return time;
        }

        public String getDate() {
            return date;
        }

        public String getFromAccount() {
            return fromAccount;
        }

        public String getToAccount() {
            return toAccount;
        }
    }

    // Getters and Setters
    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
