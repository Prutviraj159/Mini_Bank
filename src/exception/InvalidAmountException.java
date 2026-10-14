package exception;

/**
 * Thrown when an invalid (zero or negative) amount is requested for deposit/withdrawal/transfer.
 * Introduced in Practical 8 (Part B).
 */
public class InvalidAmountException extends BankException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
