package exception;

/**
 * Thrown when an account has insufficient balance/overdraft for a withdrawal or transfer.
 * Carries the shortfall amount.
 * Introduced in Practical 8 (Part B).
 */
public class InsufficientFundsException extends BankException {
    private final long shortfall;

    public InsufficientFundsException(String message, long shortfall) {
        super(message);
        this.shortfall = shortfall;
    }

    public InsufficientFundsException(long shortfall) {
        super("Withdrawal failed: short by ₹" + shortfall);
        this.shortfall = shortfall;
    }

    public long getShortfall() {
        return shortfall;
    }
}
