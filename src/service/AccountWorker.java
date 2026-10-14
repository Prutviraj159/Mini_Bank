package service;

import exception.InvalidAmountException;
import model.Account;

/**
 * Worker task executing repeated deposits for concurrency testing.
 * Introduced in Practical 9 (Part B).
 */
public class AccountWorker implements Runnable {
    private final Account account;
    private final int times;
    private final long amount;

    public AccountWorker(Account account, int times, long amount) {
        this.account = account;
        this.times = times;
        this.amount = amount;
    }

    @Override
    public void run() {
        for (int i = 0; i < times; i++) {
            try {
                account.deposit(amount);
            } catch (InvalidAmountException e) {
                System.err.println("Worker deposit error: " + e.getMessage());
            }
        }
    }
}
