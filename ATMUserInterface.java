import java.util.Scanner;

/**
 *
 * @author Iraku Harry
 * Department of Networks,
 * School of Computing and Information Technology,
 * College of Computing and Information Sciences,
 * Makerere University, Kampala
 * {@link https://github.com/iraqooh/}
 * 
 * Console application that interacts with the user
 */
public class ATMUserInterface
{
    static int loginAttempts;
    static Scanner scan;
    static ATM atm;
    
    public static void main(String[] args)
    {
        scan = new Scanner(System.in);
        int port = 0, option = 0;
        String server = null, username = null, password = null,
                firstName = null, lastName = null, accountNumber = null;
        short pin = 0;
        double deposit = 0.0d;
        try
        {
            System.out.println("Enter server properties to establish server connection\n---------------------------");
            System.out.print("\tServer: ");
            server = scan.nextLine();
            System.out.print("\tPort: ");
            port = Integer.parseInt(scan.nextLine());
            System.out.print("\tUsername: ");
            username = scan.nextLine();
            System.out.print("\tPassword: ");
            password = scan.nextLine();
            atm = new ATM(server, port, username, password);
            System.out.println("--------------------------------------------------------");
            if(atm.setConnection())
            {
                System.out.println("--------------------------------------------------------");
                System.out.println("\tWelcome to Bank ATM");
                while(true)
                {
                    System.out.println("--------------------------------------------------------");
                    System.out.print("\tAccess\n\t------\n");
                    System.out.print("1. Register\n2. Login\n3. Exit: ");
                    option = Integer.parseInt(scan.nextLine());
                    if(option == 3) break;
                    switch(option)
                    {
                        case 1:
                            System.out.println("\tRegistration\n--------------------------------------------");
                            while(true)
                            {
                                System.out.print("\tFirst Name: ");
                                firstName = scan.nextLine();
                                System.out.print("\tLast Name: ");
                                lastName = scan.nextLine();
                                System.out.print("\tInitial Deposit (min 30,000.00 units): ");
                                try
                                {
                                    deposit = Double.parseDouble(scan.nextLine());
                                    if(deposit < 30000 || deposit > 999999999.99)
                                        throw new Exception("Error: Invalid amount");
                                    else
                                    {
                                        atm.createAccount(firstName, lastName, 
                                                "", (short)0, deposit);
                                        System.out.print("Creating account...");
                                        if(atm.register())
                                        {
                                            System.out.println("Your account is ready");
                                            dashboard(atm);
                                        }
                                        else System.out.println("Registration failed");
                                    }
                                    break;
                                }
                                catch(NumberFormatException e)
                                {
                                    System.out.println("Error: non-digit entry");
                                }
                                catch(Exception e)
                                {
                                    System.out.println(e.getMessage());
                                }
                                System.out.println("----------------------------------------------------");
                            }
                            break;
                        case 2:
                            System.out.println("----------------------------------------------------");
                            System.out.print("Sign in\n\tAccount number: ");
                            accountNumber = scan.nextLine();
                            try
                            {
                                System.out.print("\tPIN: ");
                                pin = Short.parseShort(scan.nextLine());
                                System.out.println("----------------------------------------------------");
                                if(atm.login(accountNumber, pin))
                                        dashboard(atm);
                            }
                            catch(NumberFormatException e)
                            {
                                System.out.println("Non-digit entry");
                            }
                            break;
                        default:
                            System.out.println("Invalid input");
                    }
                }
            }
        }
        catch(NumberFormatException e)
        {
            System.out.println("Non-digit entry");
        }
        if(atm != null) atm.clean();
        scan.close();
    }
    
    /**
     * Presents the services for an authenticated user
     * @param atm this instance of the ATM
     */
    public static void dashboard(ATM atm)
    {
        System.out.println("--------Welcome " + atm.account.getFirstName() + "----------");
        int opt = 0;
        double deposit = 0d, withdrawal = 0.0d;
        while(true)
        {
            System.out.println("--------------------------------------------------------");
            System.out.print("\tAccount Menu\n\t------------\n1. Deposit\n2. " + 
                    "Withdrawal\n3. Check Balance\n4. Account Summary\n5. " + 
                    "Change PIN\n6. Logout: ");
            try
            {
                opt = Integer.parseInt(scan.nextLine());
            }
            catch(NumberFormatException e)
            {
                System.out.println("Error: Non-digit input");
            }
            switch(opt)
            {
                case 1:
                    while(true)
                    {
                        System.out.println("--------------------------------------------");
                        System.out.print("\tDeposit amount: ");
                        try
                        {
                            deposit = Double.parseDouble(scan.nextLine());
                            if(deposit < 1000 || (deposit + atm.account.getBalance()) > 999999999.99)
                            {
                                System.out.println("Deposit error");
                                continue;
                            }
                            if(atm.deposit(deposit))
                                    System.out.println("Deposit success");
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Error: Non-digit entry");
                        }
                        break;
                    }
                    break;
                case 2:
                    while(true)
                    {
                        System.out.println("--------------------------------------------");
                        if(atm.account.getBalance() < 2000)
                        {
                            System.out.println("Insufficient funds");
                            break;
                        }
                        System.out.print("\tWithdrawal Amount (minimum 1000, maximum " +
                                (atm.account.getBalance() - 1000) + "): ");
                        try
                        {
                            withdrawal = Double.parseDouble(scan.nextLine());
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Error: Non-digit input");
                        }
                        if(withdrawal > atm.account.getBalance() || 
                                (atm.account.getBalance() - withdrawal < 1000)
                                || withdrawal < 1000)
                        {
                            System.out.println("Withdrawal error");
                            continue;
                        }
                        else
                        {
                            atm.withdraw(withdrawal);
                            System.out.println("Withdrawal success");
                        }
                        break;
                    }
                    break;
                case 3:
                    System.out.println("------------------------------------------------");
                    System.out.printf("\tAccount Balance: %,8.2f\n", 
                            atm.checkBalance());
                    break;
                case 4:
                    System.out.println("------------------------------------------------");
                    System.out.printf("\t%s %32s %s\n\t%s %26s\n\t%s %32d\n\t%s\t\t\t\t%,.2f\n\t",
                            "Name", atm.account.getFirstName(),
                            atm.account.getLastName(), "Account Number", 
                            atm.account.getAccountNumber(), "PIN",
                            atm.account.getPIN(), "Balance", atm.account.getBalance()
                            );
                    break;
                case 5:
                    short newPin = 0;
                    while(true)
                    {
                        try
                        {
                            System.out.println("--------------------------------------------");
                            System.out.print("\tNew PIN: ");
                            newPin = Short.parseShort(scan.nextLine());
                            if(newPin >= 1000 && newPin <= 9999)
                            {
                                if(newPin != atm.account.getPIN())
                                {
                                    atm.changePIN(newPin);
                                    System.out.println("Pin changed");
                                }
                                else System.out.println("Same as old pin");
                                break;
                            }
                            else throw new Exception("Must be 4 digits");
                        }
                        catch(NumberFormatException e)
                        {
                            System.out.println("Error: Non-digit entry");
                        }
                        catch(Exception e)
                        {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case 6:
                    System.out.print("Logging out...");
                    break;
                default:
                    System.out.println("Invalid input");
            }
            if(opt == 6) break;
        }
    }
}