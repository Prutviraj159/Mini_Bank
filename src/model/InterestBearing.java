package model;

/**
 * Interface for interest-bearing accounts.
 * Provides default calculation methods for annual and monthly interest.
 * Introduced in Practical 6 (Part B).
 */
public interface InterestBearing {
    double interestRate();
    long getBalance();

    /**
     * Calculates projected yearly interest based on current balance and interest rate.
     */
    default double yearlyInterest() {
        return (getBalance() * interestRate()) / 100.0;
    }

    /**
     * Supplementary method: Calculates projected monthly interest.
     */
    default double monthlyInterest() {
        return yearlyInterest() / 12.0;
    }
}
