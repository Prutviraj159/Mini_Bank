package service;

import exception.*;
import model.*;
import model.annotation.Audit;
import util.StatementFormatter;
import util.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Core banking service layer wiring Repository, Validator, Exceptions,
 * Concurrency, Persistence, and Reporting.
 * Introduced in Practical 14 (Part B) as the Capstone Integration Service.
 */
public class BankService {

    private final Repository<Account> accountRepository;
    private final Repository<Customer> customerRepository;
    private final TransactionProcessor transactionProcessor;

    private final Path dataDirectory;
    private final Path stateFile;
    private final Path logFile;
    private final Path reportFile;

    public BankService() {
        this(Paths.get("data"));
    }

    public BankService(Path baseDataDir) {
        this.accountRepository = new Repository<>();
        this.customerRepository = new Repository<>();
        this.transactionProcessor = new TransactionProcessor();

        this.dataDirectory = baseDataDir;
        this.stateFile = baseDataDir.resolve("accounts.dat");
        this.logFile = baseDataDir.resolve("logs").resolve("transactions.log");
        this.reportFile = baseDataDir.resolve("reports").resolve("daily_report.txt");

        // Load existing persisted state if available
        loadStateSilently();
    }

    @Audit(action = "OPEN_ACCOUNT")
    public Account openAccount(String accountType, String ownerName, long initialBalance, Customer customer)
            throws InvalidAmountException, IllegalArgumentException {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account owner name cannot be empty");
        }
        if (initialBalance < 0) {
            throw new InvalidAmountException("Opening balance cannot be negative: ₹" + initialBalance);
        }

        if (customer != null) {
            customerRepository.save(customer);
        }

        Account account;
        String type = accountType.trim().toUpperCase();
        switch (type) {
            case "SAVINGS", "SAVING", "1" -> account = new SavingsAccount(ownerName, initialBalance, 500, customer);
            case "CURRENT", "2" -> account = new CurrentAccount(ownerName, initialBalance, 5000, customer);
            case "FIXED_DEPOSIT", "FD", "3" -> account = new FixedDepositAccount(ownerName, initialBalance, 12, customer);
            default -> throw new IllegalArgumentException("Unknown account type: " + accountType + ". Choose SAVINGS, CURRENT, or FIXED_DEPOSIT.");
        }

        accountRepository.save(account);
        transactionProcessor.registerSharedAccount(account);
        logTransaction("OPEN_ACCOUNT " + account.getAccountNumber() + " initial=" + initialBalance + " type=" + account.getAccountType());

        return account;
    }

    @Audit(action = "DEPOSIT")
    public void deposit(String accountNumber, long amount) throws BankException {
        Account account = getAccountOrThrow(accountNumber);
        account.deposit(amount);
        logTransaction("DEPOSIT " + accountNumber + " " + amount + " new_balance=" + account.getBalance());
    }

    @Audit(action = "WITHDRAW")
    public void withdraw(String accountNumber, long amount) throws BankException {
        Account account = getAccountOrThrow(accountNumber);
        account.withdraw(amount);
        logTransaction("WITHDRAW " + accountNumber + " " + amount + " new_balance=" + account.getBalance());
    }

    @Audit(action = "TRANSFER")
    public void transfer(String fromAccountNumber, String toAccountNumber, long amount) throws BankException {
        Account from = getAccountOrThrow(fromAccountNumber);
        Account to = getAccountOrThrow(toAccountNumber);

        from.transfer(to, amount);
        logTransaction("TRANSFER from=" + fromAccountNumber + " to=" + toAccountNumber + " amount=" + amount);
    }

    @Audit(action = "STATEMENT")
    public String statement(String accountNumber) throws AccountNotFoundException {
        Account account = getAccountOrThrow(accountNumber);
        return StatementFormatter.buildStatement(account);
    }

    public Account getAccount(String accountNumber) {
        return accountRepository.findById(accountNumber);
    }

    public List<Account> listAccounts() {
        return accountRepository.findAll();
    }

    /**
     * Practical 12 Requirement: Accounts sorted by balance using a Comparator.
     */
    public List<Account> listAccountsSortedByBalance() {
        List<Account> list = accountRepository.findAll();
        list.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return list;
    }

    /**
     * Practical 10 & 14: Submits a batch of transaction tasks to the thread pool.
     */
    public void processBatch(List<Runnable> tasks) {
        if (tasks == null) return;
        for (Runnable task : tasks) {
            transactionProcessor.submit(task);
        }
    }

    /**
     * Practical 11 & 14: Generates the NIO audit summary report.
     */
    @Audit(action = "GENERATE_REPORT")
    public String generateDailyReport() throws IOException {
        return ReportGenerator.generateReport(dataDirectory.resolve("logs"), reportFile);
    }

    /**
     * Practical 11: Saves accounts to stateFile using ObjectOutputStream.
     */
    public synchronized void saveState() throws IOException {
        List<Account> accounts = accountRepository.findAll();
        StatePersister.save(accounts, stateFile);
        System.out.println("[PERSISTENCE] Successfully saved " + accounts.size() + " accounts to " + stateFile);
    }

    /**
     * Practical 11: Loads accounts from stateFile using ObjectInputStream.
     */
    public synchronized void loadState() throws IOException, ClassNotFoundException {
        if (!Files.exists(stateFile)) {
            return;
        }
        Account[] accounts = StatePersister.load(stateFile);
        long maxCounter = 0;
        for (Account acc : accounts) {
            accountRepository.save(acc);
            transactionProcessor.registerSharedAccount(acc);
            if (acc.getCustomer() != null) {
                customerRepository.save(acc.getCustomer());
            }
            try {
                long num = Long.parseLong(acc.getAccountNumber().replaceAll("\\D+", ""));
                if (num > maxCounter) {
                    maxCounter = num;
                }
            } catch (Exception ignored) {}
        }
        Account.resetCounter(maxCounter);
        System.out.println("[PERSISTENCE] Loaded " + accounts.length + " accounts from storage.");
    }

    public void shutdown() {
        try {
            saveState();
        } catch (IOException e) {
            System.err.println("Failed to persist state on exit: " + e.getMessage());
        }
        transactionProcessor.stop();
    }

    public Repository<Account> getAccountRepository() {
        return accountRepository;
    }

    public Repository<Customer> getCustomerRepository() {
        return customerRepository;
    }

    public TransactionProcessor getTransactionProcessor() {
        return transactionProcessor;
    }

    private Account getAccountOrThrow(String accountNumber) throws AccountNotFoundException {
        Account account = accountRepository.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account with number '" + accountNumber + "' was not found in the bank.");
        }
        return account;
    }

    private void logTransaction(String entry) {
        try {
            TransactionLog.append(logFile, entry);
        } catch (IOException e) {
            System.err.println("Warning: Failed to append transaction log: " + e.getMessage());
        }
    }

    private void loadStateSilently() {
        try {
            loadState();
        } catch (Exception e) {
            // Ignore error on first run when no file exists
        }
    }
}
