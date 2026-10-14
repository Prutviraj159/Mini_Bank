package model;

/**
 * Menu options available in the MiniBank application.
 * Introduced in Practical 1 (Part B), expanded for full capstone integration in Practical 14.
 */
public enum MenuOption {
    OPEN_ACCOUNT(1, "Open Account"),
    DEPOSIT(2, "Deposit Money"),
    WITHDRAW(3, "Withdraw Money"),
    TRANSFER(4, "Transfer Funds"),
    STATEMENT(5, "Account Statement"),
    LIST_ACCOUNTS(6, "List Accounts (Sorted by Balance)"),
    BATCH_TRANSACTIONS(7, "Process Batch Transactions (ThreadPool)"),
    AUDIT_REPORT(8, "Generate End-of-Day Audit Report (NIO)"),
    CONCURRENCY_DEMO(9, "Run Concurrency & Race Condition Demo"),
    REPOSITORY_DEMO(10, "Run Generic Repository Demo"),
    EXIT(11, "Save & Exit");

    private final int optionNumber;
    private final String description;

    MenuOption(int optionNumber, String description) {
        this.optionNumber = optionNumber;
        this.description = description;
    }

    public int getOptionNumber() {
        return optionNumber;
    }

    public String getDescription() {
        return description;
    }

    public static MenuOption fromChoice(int choice) {
        for (MenuOption option : values()) {
            if (option.optionNumber == choice) {
                return option;
            }
        }
        return null;
    }
}
