package model;

/**
 * Fixed Deposit Account with 7.0% interest and locked withdrawals.
 * Introduced in Practical 5 (Part B).
 */
public class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;

    private final int tenureMonths;

    public FixedDepositAccount(String ownerName, long openingBalance) {
        this(ownerName, openingBalance, 12, null);
    }

    public FixedDepositAccount(String ownerName, long openingBalance, int tenureMonths) {
        this(ownerName, openingBalance, tenureMonths, null);
    }

    public FixedDepositAccount(String ownerName, long openingBalance, int tenureMonths, Customer customer) {
        super(ownerName, openingBalance, customer);
        this.tenureMonths = tenureMonths;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        // Locked until maturity as per Practical 5
        return false;
    }

    @Override
    protected long calculateShortfall(long amount) {
        return amount; // Cannot withdraw any amount while locked
    }

    @Override
    public String getAccountType() {
        return "Fixed Deposit (7%)";
    }
}
