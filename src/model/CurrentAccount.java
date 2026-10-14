package model;

/**
 * Current Account with overdraft limit and 0.0% interest.
 * Introduced in Practical 5 (Part B).
 */
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;

    private final long overdraftLimit;

    public CurrentAccount(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, 5000, null);
    }

    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit) {
        this(ownerName, openingBalance, overdraftLimit, null);
    }

    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit, Customer customer) {
        super(ownerName, openingBalance, customer);
        this.overdraftLimit = overdraftLimit;
    }

    public long getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= -overdraftLimit;
    }

    @Override
    protected long calculateShortfall(long amount) {
        long available = getBalance() + overdraftLimit;
        return amount > available ? (amount - available) : 0;
    }

    @Override
    public String getAccountType() {
        return "Current (OD:" + overdraftLimit + ")";
    }
}
