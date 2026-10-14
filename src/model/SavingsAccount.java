package model;

/**
 * Savings Account with minimum balance requirement and 4.0% interest.
 * Introduced in Practical 5 (Part B).
 */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;

    private final long minBalance;

    public SavingsAccount(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, 500, null);
    }

    public SavingsAccount(String ownerName, long openingBalance, long minBalance) {
        this(ownerName, openingBalance, minBalance, null);
    }

    public SavingsAccount(String ownerName, long openingBalance, long minBalance, Customer customer) {
        super(ownerName, openingBalance, customer);
        this.minBalance = minBalance;
    }

    public long getMinBalance() {
        return minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= minBalance;
    }

    @Override
    protected long calculateShortfall(long amount) {
        long available = getBalance() - minBalance;
        return amount > available ? (amount - available) : 0;
    }

    @Override
    public String getAccountType() {
        return "Savings (4%)";
    }
}
