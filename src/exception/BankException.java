package exception;

/**
 * Base checked exception for all banking operations.
 * Introduced in Practical 8 (Part B).
 */
public class BankException extends Exception {
    public BankException(String message) {
        super(message);
    }

    public BankException(String message, Throwable cause) {
        super(message, cause);
    }
}
