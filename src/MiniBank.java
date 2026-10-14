import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.BankInfo;
import model.Customer;
import model.MenuOption;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.WithdrawRule;
import model.Command;
import service.AccountWorker;
import service.BankService;
import service.Repository;
import util.AnnotationValidator;
import util.BankSession;
import util.CommandParser;
import util.StatementFormatter;

import static util.Validator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for the MiniBank banking system.
 * Connects all Practicals 1 to 14 (Part B) into a unified, interactive console application.
 */
public class MiniBank {

    private static final BankInfo BANK_INFO = new BankInfo("Mini Bank", "Ahmedabad Branch");

    public static void main(String[] args) {

        System.out.println("=================================================");
        System.out.println("            " + BANK_INFO.name().toUpperCase() + " SYSTEM            ");
        System.out.println("            Branch: " + BANK_INFO.branch() + "          ");
        System.out.println("=================================================");

        // Initialize Bank Service (persists to data/ and manages concurrency)
        BankService bankService = new BankService();

        // Seed sample accounts if bank repository is empty
        seedInitialAccountsIfEmpty(bankService);

        // Try-with-resources using AutoCloseable BankSession (Practical 8 Part B)
        try (BankSession session = new BankSession("SystemAdmin")) {

            Scanner sc = new Scanner(System.in);
            int choice = -1;

            do {
                printMenu();
                System.out.print("Enter your choice (1-11): ");

                String input = sc.nextLine().trim();
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println(">> Invalid input. Please enter a number between 1 and 11.\n");
                    continue;
                }

                MenuOption option = MenuOption.fromChoice(choice);
                if (option == null) {
                    System.out.println(">> Choice out of range. Please choose from the menu.\n");
                    continue;
                }

                switch (option) {
                    case OPEN_ACCOUNT -> handleOpenAccount(bankService, sc);
                    case DEPOSIT -> handleDeposit(bankService, sc);
                    case WITHDRAW -> handleWithdraw(bankService, sc);
                    case TRANSFER -> handleTransfer(bankService, sc);
                    case STATEMENT -> handleStatement(bankService, sc);
                    case LIST_ACCOUNTS -> handleListAccounts(bankService, sc);
                    case BATCH_TRANSACTIONS -> handleBatchTransactions(bankService);
                    case AUDIT_REPORT -> handleAuditReport(bankService);
                    case CONCURRENCY_DEMO -> handleConcurrencyDemo();
                    case REPOSITORY_DEMO -> handleRepositoryDemo();
                    case EXIT -> {
                        System.out.println("\nSaving bank state to disk...");
                        bankService.shutdown();
                        System.out.println("Thank you for using " + BANK_INFO.name() + ". Goodbye!");
                    }
                }

                if (choice != 11) {
                    System.out.println("\nPress ENTER to continue...");
                    sc.nextLine();
                }

            } while (choice != 11);

        } catch (Exception e) {
            System.err.println("Fatal Application Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void printMenu() {
        System.out.println("\n=================== MAIN MENU ===================");
        for (MenuOption opt : MenuOption.values()) {
            System.out.printf(" %2d. %s\n", opt.getOptionNumber(), opt.getDescription());
        }
        System.out.println("=================================================");
    }

    private static void handleOpenAccount(BankService service, Scanner sc) {
        System.out.println("\n--- [1] OPEN NEW ACCOUNT ---");
        System.out.println("Select Account Type:");
        System.out.println("  1. Savings Account (4.0% p.a. | Min Balance: ₹500)");
        System.out.println("  2. Current Account (0.0% p.a. | Overdraft: ₹5,000)");
        System.out.println("  3. Fixed Deposit   (7.0% p.a. | Locked)");
        System.out.print("Choice (1-3): ");
        String typeChoice = sc.nextLine().trim();

        String type = switch (typeChoice) {
            case "1" -> "SAVINGS";
            case "2" -> "CURRENT";
            case "3" -> "FIXED_DEPOSIT";
            default -> "SAVINGS";
        };

        System.out.print("Enter Customer Name: ");
        String name = sc.nextLine().trim();

        // Mobile validation using compiled Pattern (Practical 4)
        String mobile;
        while (true) {
            System.out.print("Enter 10-digit Mobile (starts with 6-9): ");
            mobile = sc.nextLine().trim();
            if (isValidMobile(mobile)) break;
            System.out.println(">> Invalid mobile number format. Please re-enter.");
        }

        // Email validation using compiled Pattern (Practical 4)
        String email;
        while (true) {
            System.out.print("Enter Email Address: ");
            email = sc.nextLine().trim();
            if (isValidEmail(email)) break;
            System.out.println(">> Invalid email address format. Please re-enter.");
        }

        // Address nested class input (Practical 3)
        System.out.print("Enter Street / Address Line: ");
        String line = sc.nextLine().trim();
        System.out.print("Enter City: ");
        String city = sc.nextLine().trim();
        System.out.print("Enter Pincode: ");
        String pin = sc.nextLine().trim();

        Customer.Address address = new Customer.Address(line, city, pin);
        Customer customer = new Customer(name, email, mobile, address);

        long balance = 0;
        while (true) {
            System.out.print("Enter Initial Deposit Amount (₹): ");
            String amtStr = sc.nextLine().trim();
            if (isValidAmount(amtStr)) {
                balance = Long.parseLong(amtStr);
                break;
            }
            System.out.println(">> Amount must be a positive integer.");
        }

        try {
            Account account = service.openAccount(type, name, balance, customer);
            System.out.println("\n✓ Account Successfully Opened!");
            System.out.println("  Account Number: " + account.getAccountNumber());
            System.out.println("  Customer ID   : " + customer.getCustomerId());
            System.out.println("  Account Type  : " + account.getAccountType());
            System.out.println("  Balance       : ₹" + account.getBalance());

            // Run reflection validator on new account (Practical 7)
            String[] errors = AnnotationValidator.validate(account);
            if (errors.length == 0) {
                System.out.println("  [Reflection Check] @Positive and @MaxLength validated: OK");
            } else {
                for (String err : errors) {
                    System.out.println("  [Validation Warning] " + err);
                }
            }

        } catch (Exception e) {
            System.err.println("Failed to open account: " + e.getMessage());
        }
    }

    private static void handleDeposit(BankService service, Scanner sc) {
        System.out.println("\n--- [2] DEPOSIT MONEY ---");
        System.out.println("Tip: You can enter standard format (e.g. 'AC0001 500') or type 'cmd' for command parsing.");
        System.out.print("Enter Account Number (or 'cmd' to enter full command): ");
        String input = sc.nextLine().trim();

        String accNum;
        long amount;

        if ("cmd".equalsIgnoreCase(input)) {
            System.out.print("Enter raw command line (e.g., DEPOSIT AC0001 500): ");
            String rawCmd = sc.nextLine().trim();
            try {
                Command cmd = CommandParser.parse(rawCmd);
                accNum = cmd.accountNumber();
                amount = cmd.amount();
            } catch (IllegalArgumentException e) {
                System.out.println(">> Parse error: " + e.getMessage());
                return;
            }
        } else {
            accNum = input;
            System.out.print("Enter Deposit Amount (₹): ");
            try {
                amount = Long.parseLong(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(">> Invalid amount format.");
                return;
            }
        }

        try {
            service.deposit(accNum, amount);
            Account acc = service.getAccount(accNum);
            System.out.printf("✓ Deposited ₹%,d into %s. New Balance: ₹%,d\n", amount, accNum, acc.getBalance());
        } catch (BankException e) {
            System.err.println("Deposit Failed: " + e.getMessage());
        }
    }

    private static void handleWithdraw(BankService service, Scanner sc) {
        System.out.println("\n--- [3] WITHDRAW MONEY ---");
        System.out.print("Enter Account Number: ");
        String accNum = sc.nextLine().trim();

        System.out.print("Enter Withdrawal Amount (₹): ");
        long amount;
        try {
            amount = Long.parseLong(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(">> Invalid amount format.");
            return;
        }

        // Functional interface WithdrawRule demonstration (Practical 6)
        WithdrawRule dailyLimitRule = (account, amt) -> amt <= 50000;
        Account targetAcc = service.getAccount(accNum);
        if (targetAcc != null && !dailyLimitRule.allow(targetAcc, amount)) {
            System.out.println(">> [WithdrawRule Check] Amount exceeds daily transaction cap of ₹50,000.");
            return;
        }

        try {
            service.withdraw(accNum, amount);
            Account acc = service.getAccount(accNum);
            System.out.printf("✓ Withdrawn ₹%,d from %s. New Balance: ₹%,d\n", amount, accNum, acc.getBalance());
        } catch (InsufficientFundsException e) {
            // Practical 8 Requirement: catch InsufficientFundsException and report shortfall
            System.err.printf("✗ Withdrawal Failed: %s (Shortfall: ₹%,d)\n", e.getMessage(), e.getShortfall());
        } catch (InvalidAmountException e) {
            System.err.println("✗ Invalid Amount: " + e.getMessage());
        } catch (BankException e) {
            System.err.println("✗ Banking Error: " + e.getMessage());
        }
    }

    private static void handleTransfer(BankService service, Scanner sc) {
        System.out.println("\n--- [4] TRANSFER FUNDS ---");
        System.out.print("Enter Source Account Number: ");
        String fromAcc = sc.nextLine().trim();

        System.out.print("Enter Target Account Number: ");
        String toAcc = sc.nextLine().trim();

        System.out.print("Enter Amount to Transfer (₹): ");
        long amount;
        try {
            amount = Long.parseLong(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(">> Invalid amount format.");
            return;
        }

        try {
            service.transfer(fromAcc, toAcc, amount);
            System.out.printf("✓ Successfully transferred ₹%,d from %s to %s!\n", amount, fromAcc, toAcc);
            System.out.printf("  %s Balance: ₹%,d | %s Balance: ₹%,d\n",
                    fromAcc, service.getAccount(fromAcc).getBalance(),
                    toAcc, service.getAccount(toAcc).getBalance());
        } catch (InsufficientFundsException e) {
            System.err.printf("✗ Transfer Failed: %s (Shortfall: ₹%,d)\n", e.getMessage(), e.getShortfall());
        } catch (BankException e) {
            System.err.println("✗ Transfer Error: " + e.getMessage());
        }
    }

    private static void handleStatement(BankService service, Scanner sc) {
        System.out.println("\n--- [5] ACCOUNT STATEMENT ---");
        System.out.print("Enter Account Number: ");
        String accNum = sc.nextLine().trim();

        try {
            String statement = service.statement(accNum);
            System.out.println("\n" + statement);
        } catch (Exception e) {
            System.err.println("Statement Error: " + e.getMessage());
        }
    }

    private static void handleListAccounts(BankService service, Scanner sc) {
        System.out.println("\n--- [6] LIST OF ACCOUNTS ---");
        System.out.print("Sort accounts by balance descending? (y/n): ");
        String sortChoice = sc.nextLine().trim();

        List<Account> accounts;
        if ("y".equalsIgnoreCase(sortChoice)) {
            // Practical 12 Requirement: sortedByBalance using Comparator
            accounts = service.listAccountsSortedByBalance();
            System.out.println("Accounts (Sorted by Balance Descending):");
        } else {
            accounts = service.listAccounts();
            System.out.println("Accounts (Standard order):");
        }

        if (accounts.isEmpty()) {
            System.out.println("  (No accounts registered yet)");
            return;
        }

        System.out.println("-----------------------------------------------------------------------------------");
        System.out.printf("%-8s | %-15s | %-20s | %-12s | %s\n", "Acc No", "Owner", "Type", "Balance", "Interest");
        System.out.println("-----------------------------------------------------------------------------------");
        for (Account a : accounts) {
            System.out.printf("%-8s | %-15s | %-20s | ₹%,-11d | %.1f%% (₹%,.2f/yr)\n",
                    a.getAccountNumber(), a.getOwnerName(), a.getAccountType(), a.getBalance(),
                    a.interestRate(), a.yearlyInterest());
        }
        System.out.println("-----------------------------------------------------------------------------------");
    }

    private static void handleBatchTransactions(BankService service) {
        System.out.println("\n--- [7] CONCURRENT BATCH TRANSACTIONS (PRACTICAL 10) ---");
        List<Account> accounts = service.listAccounts();
        if (accounts.size() < 2) {
            System.out.println(">> Need at least 2 accounts to run batch transactions.");
            return;
        }

        Account a1 = accounts.get(0);
        Account a2 = accounts.get(1);

        System.out.println("Submitting 10 concurrent deposit/transfer tasks to TransactionProcessor thread pool...");

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            final int index = i;
            tasks.add(() -> {
                try {
                    a1.deposit(100);
                    System.out.printf("[Pool Thread: %s] Deposited ₹100 into %s\n",
                            Thread.currentThread().getName(), a1.getAccountNumber());
                } catch (Exception e) {
                    System.err.println("Batch error: " + e.getMessage());
                }
            });
            tasks.add(() -> {
                try {
                    a2.deposit(200);
                    System.out.printf("[Pool Thread: %s] Deposited ₹200 into %s\n",
                            Thread.currentThread().getName(), a2.getAccountNumber());
                } catch (Exception e) {
                    System.err.println("Batch error: " + e.getMessage());
                }
            });
        }

        service.processBatch(tasks);
        System.out.println("✓ All 10 tasks submitted to ExecutorService thread pool!");
    }

    private static void handleAuditReport(BankService service) {
        System.out.println("\n--- [8] GENERATE END-OF-DAY AUDIT REPORT (PRACTICAL 11) ---");
        try {
            String report = service.generateDailyReport();
            System.out.println(report);
            System.out.println("✓ Audit report generated and saved to data/reports/daily_report.txt");
        } catch (Exception e) {
            System.err.println("Report Generation Failed: " + e.getMessage());
        }
    }

    private static void handleConcurrencyDemo() {
        System.out.println("\n--- [9] CONCURRENCY & RACE CONDITION DEMO (PRACTICAL 9) ---");
        System.out.println("Testing Thread Safety: 10 threads, each depositing ₹1 one thousand times.");

        SavingsAccount testAccount = new SavingsAccount("RaceConditionDemo", 0);
        int threadCount = 10;
        int depositsPerThread = 1000;

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new AccountWorker(testAccount, depositsPerThread, 1), "TestThread-" + (i + 1));
            threads[i].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ignored) {}
        }

        long expected = threadCount * depositsPerThread;
        System.out.printf("Expected Balance: ₹%,d | Actual Balance: ₹%,d\n", expected, testAccount.getBalance());
        if (testAccount.getBalance() == expected) {
            System.out.println("✓ Thread-safety confirmed! Synchronization prevented all race conditions.");
        } else {
            System.out.println("✗ Race condition detected! Expected balance does not match.");
        }
    }

    private static void handleRepositoryDemo() {
        System.out.println("\n--- [10] GENERIC REPOSITORY & BOUNDED TYPES (PRACTICAL 13) ---");
        Repository<Account> accountRepo = new Repository<>();
        SavingsAccount sa = new SavingsAccount("User X", 5000);
        CurrentAccount ca = new CurrentAccount("User Y", 8000);

        accountRepo.saveAll(sa, ca);
        System.out.println("Saved 2 accounts into generic Repository<Account> via reflection on @Id.");

        Account found = accountRepo.findById(sa.getAccountNumber());
        System.out.println("findById('" + sa.getAccountNumber() + "') -> " + (found != null ? found.getOwnerName() : "null"));

        Repository<Customer> customerRepo = new Repository<>();
        Customer c1 = new Customer("Pooja", "pooja@test.com", "9876501234");
        customerRepo.save(c1);
        System.out.println("Saved Customer into generic Repository<Customer> -> ID: " + c1.getCustomerId());

        // Bounded type generic method test (Practical 13 Requirement 4)
        Double[] rates = {4.0, 7.0, 0.0, 6.5};
        double avgRate = Repository.average(rates);
        System.out.printf("Bounded generic average([4.0, 7.0, 0.0, 6.5]) = %.2f%%\n", avgRate);
    }

    private static void seedInitialAccountsIfEmpty(BankService service) {
        if (service.listAccounts().isEmpty()) {
            try {
                Customer c1 = new Customer("Rahul Sharma", "rahul@gmail.com", "9876543210",
                        new Customer.Address("12 Ring Road", "Ahmedabad", "380015"));
                Customer c2 = new Customer("Priya Patel", "priya@gmail.com", "9123456789",
                        new Customer.Address("45 SG Highway", "Gandhinagar", "382010"));
                Customer c3 = new Customer("Amit Verma", "amit@yahoo.com", "9988776655",
                        new Customer.Address("78 Station Road", "Vadodara", "390001"));

                service.openAccount("SAVINGS", "Rahul Sharma", 15000, c1);
                service.openAccount("CURRENT", "Priya Patel", 25000, c2);
                service.openAccount("FIXED_DEPOSIT", "Amit Verma", 50000, c3);

            } catch (Exception e) {
                System.err.println("Warning: Could not seed initial accounts: " + e.getMessage());
            }
        }
    }
}
