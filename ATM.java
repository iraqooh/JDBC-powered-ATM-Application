package iraqooh.com.github;

import java.util.Random;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 * @author Iraku Harry,
 * Department of Networks,
 * School of Computing and Information Technology,
 * College of Computing and Information Sciences,
 * Makerere University, Kampala
 * {@link https://github.com/iraqooh/}
 */


/*
 * The ATM class contains logic for connecting to a MySQL server and database 
 * and performing writing to and reading from database operations relevant to
 * ATM transactions
 */
public class ATM
{
    //list of server connection parameters
    private String server;
    private int port;
    private String url;
    private String username;
    private String password;

    //list of database connection parameters
    private Connection connection;
    private Statement statement;
    private PreparedStatement pstat;
    private ResultSet result;

    //the bank account currently being accessed by this instance of the ATM
    /**
     *
     */
    public Account account;

    /*
     * Nested inner class Account defines the structure of a bank account and
     * provides an interface for accessing and updating its core fields
     */
    static class Account
    {
        private final String firstName;
        private final String lastName;
        private final String accountNumber;
        private short pin;
        private double balance;

        /*
         * Triple parameter constructor creates and initializes an instance of
         * Account, used when registering a new user
         * @param   firstName the first name of the account holder
         * @param   lastName the last name of the account holder
         * @param   firstDeposit the initial deposit
        */
        public Account(String firstName, String lastName, double firstDeposit)
        {
            this.firstName = firstName;
            this.lastName = lastName;
            this.balance = firstDeposit;
            this.accountNumber = Integer.toString(
                    new Random().nextInt(899999999) + 100000000);
            this.pin = (short)(new Random().nextInt(8999) + 1000);
        }

        /*
         * 4-parameter constructor creates and initializes an instance of
         * Account, used loading an existing account from the database into the
         * application
         * @param   firstName the first name of the account holder
         * @param   lastName the last name of the account holder
         * @param   firstDeposit the initial deposit
        */
        public Account(String firstName, String lastName,
                String accountNumber, short pin, double firstDeposit)
        {
            this.firstName = firstName;
            this.lastName = lastName;
            this.balance = firstDeposit;
            this.accountNumber = accountNumber;
            this.pin = pin;
        }

        //list of getter and setter definitions for the private fields of Account
        public String getFirstName()
        {
            return this.firstName;
        }

        public String getLastName()
        {
            return this.lastName;
        }

        public String getAccountNumber()
        {
            return this.accountNumber;
        }

        public short getPIN()
        {
            return this.pin;
        }

        public double getBalance()
        {
            return this.balance;
        }

        public void setPIN(short pin)
        {
            this.pin = pin;
        }

        public void setBalance(double balance)
        {
            this.balance = balance;
        }
    }
    
    /**
     * 4-parameter constructor creates and initializes an instance of
     * ATM, used launching the ATM application after establishing connection
     * with the local server and database
     * 
     * @param server server the name of the server such as 'localhost' for the 
     *        local server
     * @param port the number of the application sending and receiving network
    *           traffic from the ATM
     * @param username the name of the authenticated user for the server
     * @param password for getting access for the user to the local server
     */
    public ATM(String server, int port, String username, String password)
    {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
        this.url = "jdbc:mysql://" + this.server + ": " + this.port;
    }

    /**
     * Establishes a connection to the server by registering the MySQL
     * connector and connecting using the <code>url<code>.
     * 
     * @return {@code true} if the connection succeeds.
     */
    @SuppressWarnings("empty-statement")
    public boolean setConnection()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("connecting to server...");
            this.connection = DriverManager.getConnection(this.url,
                    this.username, this.password);
            System.out.println("connected to server");

            this.statement = this.connection.createStatement();
            if(this.statement.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS atm2") == 0)
            {
                System.out.println("New database 'atm' created");
            }
            System.out.println("connecting to ATM...");
            if(this.statement.executeUpdate("USE atm2") == 0)
            {
                System.out.println("connected to ATM");
            }
            this.statement.executeUpdate("CREATE TABLE IF NOT EXISTS accounts(" + 
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "first_name VARCHAR(16) NOT NULL, " +
                    "last_name VARCHAR(16) NOT NULL, " +
                    "acc_number VARCHAR(12) NOT NULL, " +
                    "pin SMALLINT NOT NULL, " +
                    "balance DECIMAL(9, 2) DEFAULT 30000.00)");
            return true;
        }
        catch(ClassNotFoundException e){System.out.println(
                "Driver registration error");}
        catch(SQLException e){System.out.println("Connection error");};
        return false;
    }
    
    /**
     * Closes all the open connection and SQL objects to prevent memory 
     * leakage instead of leaving it to the JVM's garbage collector
     */
    public void clean()
    {
        try
        {
            if(this.connection != null) this.connection.close();
            if(this.statement != null) this.statement.close();
            if(this.result != null) this.result.close();
            if(this.pstat != null) this.pstat.close();
        }
        catch(SQLException e)
        {
            e.getMessage();
        }
    }

    /**
     *
     * @param firstName first name of the account holder
     * @param lastName last name of the account holder
     * @param accountNumber account number of this account
     * @param pin the PIN code for accessing this account
     * @param balance the current account balance
     */
    public void createAccount(String firstName, String lastName, 
            String accountNumber, short pin, double balance)
    {
        if(pin == 0 || accountNumber == null || accountNumber.equals(""))
            this.account = new Account(firstName, lastName, balance);
        else
            this.account = new Account(firstName, lastName,
                                            accountNumber, pin, balance);
    }
    
    /**
     * Performs the update of the bank's database by writing the details of a
     * new bank account and returns {@code true} if the process succeeds
     * @return {@code true} if the registration succeeds
     */
    public boolean register()
    {
        try
        {
            this.pstat = this.connection.prepareStatement(
                    "INSERT INTO accounts(first_name," + 
                    " last_name, acc_number, pin, balance) VALUES(" +
                    "?, ?, ?, ?, ?)");
            this.pstat.setString(1, this.account.getFirstName());
            this.pstat.setString(2, this.account.getLastName());
            this.pstat.setString(3, this.account.getAccountNumber());
            this.pstat.setShort(4, this.account.getPIN());
            this.pstat.setDouble(5, this.account.getBalance());
            this.pstat.execute();
            return true;
        }
        catch(SQLException e)
        {
            System.out.println("Account creation error");
        }
        return false;
    }

    /**
     * Uses the account number and PIN provided by a user to provide access to
     * their bank account and returns {@code true} if the database has
     * granted access
     * 
     * @param accountNumber account number
     * @param pin PIN
     * 
     * @return {@code true} if the login attempt succeeds
     */
    public boolean login(String accountNumber, short pin)
    {
        String firstName = null, lastName = null;
        double balance = 0.0d;
        try
        {
            this.statement = this.connection.createStatement(1004, 1007);
            this.result = this.statement.executeQuery(
                    "SELECT * FROM accounts");

            while(this.result.next())
            {
                    if(accountNumber.equals(this.result.getString(
                            "acc_number")))
                    {
                            if(pin == this.result.getShort("pin"))
                            {
                                    firstName = this.result.getString(
                                            "first_name");
                                    balance = this.result.getDouble(
                                            "balance");
                                    lastName = this.result.getString(
                                            "last_name");
                                    this.account = new Account(firstName,
                                            lastName, accountNumber, pin,
                                            balance);
                                    return true;
                            }
                            else
                                System.out.println(
                                        "Account number and pin mismatch");
                    }
            }
            throw new Exception("Account does not exist");
        }
        catch(SQLException e)
        {
            System.out.println("Log in error");
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Returns the current account balance for an authenticated user
     * @return the current account balance
     */
    public double checkBalance()
    {
        return account.balance;
    }

    /**
     * Changes the current PIN for this account to the one provided by the user
     * and returns {@code true} if the change succeeds
     * 
     * @param newPIN the new PIN
     * @return {@code true} if the new PIN has been saved to the database
     */
    public boolean changePIN(short newPIN)
    {
        this.account.setPIN(newPIN);
        try
        {
            this.pstat = this.connection.prepareStatement("UPDATE accounts"
                    + " SET pin = ? WHERE acc_number = ?");
            this.pstat.setString(2, account.getAccountNumber());
            this.pstat.setShort(1, account.getPIN());
            this.pstat.execute();
            return true;
        }
        catch(SQLException e)
        {
            System.out.println("PIN update error");
        }
        return false;
    }

    /**
     * Credits the user's account with the amount provided in the parameter
     * @param amount the quantity by which the current balance is incremented
     * @return {@code true} if the database has been updated with the new amount
     */
    public boolean deposit(double amount)
    {
        this.account.setBalance((this.account.getBalance() + amount));
        try
        {
            this.pstat = this.connection.prepareStatement("UPDATE accounts"
                    + " SET balance = ? WHERE acc_number = ?");
            this.pstat.setString(2, this.account.getAccountNumber());
            this.pstat.setDouble(1, this.account.getBalance());
            this.pstat.execute();
            return true;
        }
        catch(SQLException e)
        {
            System.out.println("deposit error");
        }
        return false;
    }

    /**
     * Debits the user's account with the amount specified
     * @param amount the quantity by which the current balance is decreased
     * @return {@code true} if the database has been updated with the new 
     *          account balance
     */
    public boolean withdraw(double amount)
    {
        this.account.setBalance((this.account.getBalance() - amount));
        try
        {
            this.pstat = this.connection.prepareStatement("UPDATE accounts"
                    + " SET balance = ? WHERE acc_number = ?");
            this.pstat.setString(2, this.account.getAccountNumber());
            this.pstat.setDouble(1, this.account.getBalance());
            this.pstat.execute();
            return true;
        }
        catch(SQLException e)
        {
            System.out.println("Withdrawal error");
        }
        return false;
    }
}
