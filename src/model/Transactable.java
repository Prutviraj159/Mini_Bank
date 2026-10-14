package model;

import exception.BankException;

/**
 * Interface defining transactional capabilities for bank accounts.
 * Introduced in Practical 6 (Part B), enhanced with exceptions in Practical 8.
 */
public interface Transactable {
    void deposit(long amount) throws BankException;
    boolean withdraw(long amount) throws BankException;
}
