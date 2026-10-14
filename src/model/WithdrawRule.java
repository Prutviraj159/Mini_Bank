package model;

/**
 * Functional interface to define custom withdrawal rules (e.g., daily limit, VIP bonus).
 * Introduced in Practical 6 (Part B), annotated with @FunctionalInterface in Practical 7.
 */
@FunctionalInterface
public interface WithdrawRule {
    boolean allow(Account account, long amount);
}
