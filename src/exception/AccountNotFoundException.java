package exception;

/**
 * Thrown when an account number does not exist in the bank repository.
 * Introduced in Practical 8 (Part B).
 */
public class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
