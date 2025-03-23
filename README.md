# Online Banking System

A comprehensive Java-based online banking system with secure user authentication, account management, and transaction capabilities.

## Overview

This application was developed as a Java project for managing online banking operations. It provides a secure platform for users to manage their checking and savings accounts, perform transactions, and view their account information.

## Features

- **User Authentication**: Secure login and signup functionalities
- **Account Management**: Create and manage checking and savings accounts
- **Transactions**:
  - Deposit funds to checking or savings accounts
  - Withdraw funds from accounts
  - Transfer funds between accounts
- **Account Overview**: View balances for checking and savings accounts
- **Transaction History**: Search and view transaction history by date range
- **Security**: Password protection and session management

## Project Structure

```
online-banking-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── banking/
│   │   │           ├── controller/   # Controllers handling HTTP requests
│   │   │           ├── model/        # Business domain objects
│   │   │           ├── service/      # Business logic services
│   │   │           └── util/         # Utility classes
│   │   └── webapp/
│   │       ├── WEB-INF/              # Web application configuration
│   │       ├── css/                  # Stylesheets
│   │       └── js/                   # JavaScript files
│   └── test/
│       └── java/
│           └── com/
│               └── banking/          # Unit tests
└── pom.xml                          # Maven configuration
```

## Technology Stack

- **Backend**: Java SE
- **Database Access**: JDBC
- **Web Tier**: Servlets, JSP
- **Frontend**: HTML, CSS, JavaScript
- **Database**: SQL Server
- **Build Tool**: Maven (recommended for dependency management)

## Prerequisites

- JDK 8 or higher
- Microsoft SQL Server 
- Servlet container (Tomcat, Jetty, etc.)
- Maven (optional but recommended)

## Database Setup

The application uses the following database tables:

1. **Account**: Stores user account information
   - Columns: Username, Password, Name

2. **CheckingAccount**: Stores checking account information
   - Columns: CheckingAccountNumber, CustomerName, Balance, CustomerID

3. **SavingAccount**: Stores savings account information
   - Columns: SavingAccountNumber, CustomerName, Balance, CustomerID

4. **Transactions**: Records all transactions
   - Columns: TransactionNumber, TransactionAmount, TransactionType, TransactionTime, TransactionDate, FromAccount, ToAccount, CustomerID

SQL setup script:
```sql
CREATE TABLE Account (
    Username varchar(50) PRIMARY KEY,
    Password varchar(50) NOT NULL,
    Name varchar(100) NOT NULL
);

CREATE TABLE CheckingAccount (
    CheckingAccountNumber varchar(50) PRIMARY KEY,
    CustomerName varchar(100) NOT NULL,
    Balance float NOT NULL,
    CustomerID varchar(50) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Account(Username)
);

CREATE TABLE SavingAccount (
    SavingAccountNumber varchar(50) PRIMARY KEY,
    CustomerName varchar(100) NOT NULL,
    Balance float NOT NULL,
    CustomerID varchar(50) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Account(Username)
);

CREATE TABLE Transactions (
    TransactionNumber varchar(50) PRIMARY KEY,
    TransactionAmount float NOT NULL,
    TransactionType varchar(50) NOT NULL,
    TransactionTime varchar(50) NOT NULL,
    TransactionDate varchar(50) NOT NULL,
    FromAccount varchar(50),
    ToAccount varchar(50),
    CustomerID varchar(50) NOT NULL,
    FOREIGN KEY (CustomerID) REFERENCES Account(Username)
);
```

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/AyhamJo7/Online-Banking-System-using-Java.git
   cd online-banking-system
   ```

2. Configure the database connection in `src/main/java/com/banking/util/DatabaseUtil.java`

3. Build the project (with Maven):
   ```bash
   mvn clean package
   ```

4. Deploy the WAR file to your servlet container (e.g., Tomcat)

## Usage Examples

### Creating a User Account

1. Navigate to the application URL
2. Click on "Sign Up" to create a new user account
3. Fill in the required information (username, password, name)
4. Submit the form to create the account

### Opening a Bank Account

1. Log in with your credentials
2. Select "Open Bank Account" from the navigation menu
3. Choose the account type (Checking or Savings)
4. Enter the required information and initial deposit amount
5. Submit the form to create the new bank account

### Performing Transactions

1. Log in with your credentials
2. For deposits, select "Deposit" and specify the account and amount
3. For withdrawals, select "Withdraw" and specify the account and amount
4. For transfers, select "Transfer" and specify the source account, destination account, and amount

### Viewing Transaction History

1. Log in with your credentials
2. Select "Inquire Transactions" from the navigation menu
3. Specify the date range for the transactions you want to view
4. View the list of transactions matching your criteria

## Security Considerations

- User passwords are stored in the database. In a production environment, passwords should be hashed and salted.
- The application uses session management for user authentication.
- SQL queries are currently vulnerable to SQL injection. In a production environment, use prepared statements.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- This project was originally developed as a Java class assignment
- Special thanks to all contributors who have helped improve the project
