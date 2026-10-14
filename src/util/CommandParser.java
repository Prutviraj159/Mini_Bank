package util;

import model.Command;
import model.TransactionType;

/**
 * Parses user terminal command lines into Command objects.
 * Introduced in Practical 4 (Part B).
 */
public class CommandParser {

    /**
     * Parses a string line such as "DEPOSIT AC0001 500" into a Command record.
     * @param line Raw input string
     * @return Command record
     * @throws IllegalArgumentException if format or values are invalid
     */
    public static Command parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Command line cannot be empty");
        }

        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                "Invalid command syntax. Expected: <TYPE> <ACCOUNT_NUMBER> <AMOUNT>. Received " + parts.length + " parts: " + line
            );
        }

        TransactionType type;
        try {
            type = TransactionType.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown transaction type '" + parts[0] + "'. Must be DEPOSIT, WITHDRAW, or TRANSFER.");
        }

        String accountNumber = parts[1].toUpperCase();

        long amount;
        try {
            amount = Long.parseLong(parts[2]);
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be a positive integer, got: " + parts[2]);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + parts[2]);
        }

        return new Command(type, accountNumber, amount);
    }
}
