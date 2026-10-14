package service;

import model.Account;

import java.util.*;

/**
 * Storage manager for accounts using Java Collections Framework.
 * Introduced in Practical 12 (Part B).
 */
public class AccountStore {

    private final Map<String, Account> accounts;
    private final List<String> transactionHistory;

    public AccountStore() {
        this.accounts = new HashMap<>();
        this.transactionHistory = new ArrayList<>();
    }

    public synchronized void add(Account account) {
        if (account != null) {
            accounts.put(account.getAccountNumber(), account);
        }
    }

    public synchronized Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public synchronized Collection<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public synchronized boolean contains(String accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    public synchronized void recordTransaction(String entry) {
        transactionHistory.add(entry);
    }

    public synchronized List<String> getTransactionHistory() {
        return Collections.unmodifiableList(new ArrayList<>(transactionHistory));
    }

    /**
     * Returns an ArrayList<Account> sorted by balance in descending order using a Comparator.
     * Practical 12 Requirement 3.
     */
    public synchronized ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = new ArrayList<>(accounts.values());
        list.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return list;
    }

    /**
     * Supplementary Problem: Returns all accounts with balance greater than given threshold.
     */
    public synchronized List<Account> findAccountsAboveBalance(long minBalance) {
        List<Account> result = new ArrayList<>();
        for (Account acc : accounts.values()) {
            if (acc.getBalance() >= minBalance) {
                result.add(acc);
            }
        }
        return result;
    }

    public synchronized int size() {
        return accounts.size();
    }
}
