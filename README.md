# JDBC-Powered ATM Application

## Overview

This ATM application demonstrates basic ATM functionalities including user registration, login, balance checking, deposits, withdrawals, PIN changes, and account summary. The application uses JDBC for database operations and provides a console-based user interface.

## Project Structure

1. **ATM.java**: Contains the core logic for ATM operations, including database connection and transaction management.
2. **ATMUserInterface.java**: Provides a console-based interface for interacting with the ATM.

## Requirements

- Java Development Kit (JDK) 8 or higher
- MySQL Server
- MySQL Connector/J

## Setup

1. **Database Setup:**

   Ensure you have MySQL installed and running. Create a database named `atm2` if it does not exist. The application will automatically create the database and the `accounts` table if they do not already exist.

2. **Configuration:**

   - Update `ATM.java` with your MySQL server details (server, port, username, and password).

3. **Compile and Run:**

   ```
   javac iraqooh/com/github/ATM.java iraqooh/com/github/ATMUserInterface.java
   java iraqooh.com.github.ATMUserInterface
   ```

## Usage

Launching the Application:

Run ATMUserInterface which will prompt you to enter server properties and then allow you to interact with the ATM functionalities.

Functionalities:

Register: Create a new account with a minimum initial deposit of 30,000.00 units.

Login: Access your account using the account number and PIN.

Deposit: Add funds to your account.

Withdrawal: Withdraw funds from your account, subject to minimum and maximum limits.

Check Balance: View the current balance of your account.

Change PIN: Update your PIN, ensuring it is a 4-digit number.

Logout: End your session.

## Contributing

Feel free to contribute by opening issues or submitting pull requests on the GitHub repository.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
