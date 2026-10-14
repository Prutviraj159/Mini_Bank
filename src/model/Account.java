package model;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.MaxLength;
import model.annotation.Positive;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class for all bank accounts.
 * Meets requirements for:
 * - Practical 2: Encapsulation, static account counter, constructors, deposit/withdraw.
 * - Practical 3: Object overrides (toString, equals, hashCode).
 * - Practical 5: Abstract class with abstract interestRate() and canWithdraw(long).
 * - Practical 6: Implements Transactable, InterestBearing.
 * - Practical 7: Annotated with @Id, @Positive, @MaxLength, and @Override.
 * - Practical 8: Checked exceptions InsufficientFundsException and InvalidAmountException.
 * - Practical 9: Synchronized methods to guarantee thread-safety.
 * - Practical 10: Deadlock prevention via consistent account locking order.
 * - Practical 11: Serializable with serialVersionUID.
 * - Practical 12: Implements Comparable<Account>.
 */
public abstract class Account implements Transactable, InterestBearing, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;

    @MaxLength(50)
    private String ownerName;

    @Positive(message = "Account balance must be positive")
    private long balance;

    private boolean active;

    private Customer customer;

    private static long accountCounter = 0;

    public Account(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, null);
    }

    public Account(String ownerName) {
        this(ownerName, 0, null);
    }

    public Account(String ownerName, long openingBalance, Customer customer) {
        this.accountNumber = String.format("AC%04d", ++accountCounter);
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
        this.customer = customer;
    }

    public static synchronized void resetCounter(long start) {
        accountCounter = start;
    }

    public static synchronized long getAccountCounter() {
        return accountCounter;
    }

    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    public abstract String getAccountType();

    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be strictly greater than 0: ₹" + amount);
        }
        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be strictly greater than 0: ₹" + amount);
        }
        if (!canWithdraw(amount)) {
            long shortfall = calculateShortfall(amount);
            throw new InsufficientFundsException("Withdrawal of ₹" + amount + " exceeds limit for " + accountNumber, shortfall);
        }
        balance -= amount;
        return true;
    }

    /**
     * Calculates the shortfall amount when withdrawal is not allowed.
     */
    protected long calculateShortfall(long amount) {
        return amount > balance ? (amount - balance) : 0;
    }

    /**
     * Transfers funds to another account with deadlock prevention.
     * Uses try-catch-finally and re-throws BankException as specified in Practical 8 & 10.
     */
    public void transfer(Account to, long amount) throws BankException {
        if (to == null) {
            throw new BankException("Target account cannot be null");
        }
        if (this.accountNumber.equals(to.accountNumber)) {
            throw new BankException("Cannot transfer funds to the same account: " + accountNumber);
        }

        // Deadlock Prevention (Practical 10):
        // Always acquire locks in ascending lexicographical order of account numbers
        Account firstLock = this.accountNumber.compareTo(to.accountNumber) < 0 ? this : to;
        Account secondLock = this.accountNumber.compareTo(to.accountNumber) < 0 ? to : this;

        synchronized (firstLock) {
            synchronized (secondLock) {
                boolean withdrawSuccess = false;
                try {
                    this.withdraw(amount);
                    withdrawSuccess = true;
                    to.deposit(amount);
                } catch (BankException e) {
                    if (withdrawSuccess) {
                        // Rollback withdrawal if deposit failed
                        try {
                            this.deposit(amount);
                        } catch (InvalidAmountException ignored) {}
                    }
                    throw e;
                } finally {
                    // Audit or cleanup hook if needed
                }
            }
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public synchronized long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return String.format("%s | %-12s | %-14s | Balance: ₹%,d | %s",
                accountNumber, ownerName, getAccountType(), balance, active ? "ACTIVE" : "INACTIVE");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account other)) return false;
        return Objects.equals(accountNumber, other.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }
}
