package model;

/**
 * Record representing a parsed banking command.
 * Introduced in Practical 4 (Part B).
 */
public record Command(TransactionType type, String accountNumber, long amount) {
}
