import java.util.Scanner;

public class MiniBank {

    record BankInfo(String name, String branch) {}

    enum MenuOption {
        OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
    }

    static class Customer {
        private String name;
        private String email;
        private String mobile;
        private final String customerId;

        private static long customerCounter = 100;

        public Customer(String name, String email, String mobile) {
            this.name = name;
            this.email = email;
            this.mobile = mobile;
            this.customerId = generateCustomerId();
        }

        private static String generateCustomerId() {
            return "CUST" + (++customerCounter);
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getMobile() {
            return mobile;
        }

        public String getCustomerId() {
            return customerId;
        }
    }

    static class Account {
        private final String accountNumber;
        private String ownerName;
        private long balance;
        private boolean active;

        private static long accountCounter = 0;

        public Account(String ownerName, long openingBalance) {
            this.accountNumber = String.format("AC%04d", ++accountCounter);
            this.ownerName = ownerName;
            this.balance = openingBalance;
            this.active = true;
        }

        public Account(String ownerName) {
            this(ownerName, 0);
        }

        public void deposit(long amount) {
            balance += amount;
        }

        public boolean withdraw(long amount) {
            if (balance >= amount) {
                balance -= amount;
                return true;
            }
            return false;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public long getBalance() {
            return balance;
        }

        public boolean isActive() {
            return active;
        }
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankInfo bank = new BankInfo("Mini Bank", "Ahmedabad");

        System.out.println("===== " + bank.name() + " =====");
        System.out.println("Branch: " + bank.branch());

        Customer customer = new Customer(
            "Rahul",
            "rahul@gmail.com",
            "9876543210"
        );

        Account[] accounts = {
            new Account("Rahul", 1000),
            new Account("Amit", 2000),
            new Account("Jay")
        };

        accounts[0].deposit(500);
        accounts[0].withdraw(200);

        accounts[1].deposit(1000);
        accounts[1].withdraw(500);

        accounts[2].deposit(3000);
        accounts[2].withdraw(1000);

        System.out.println("\nCustomer Details:");
        System.out.println("ID: " + customer.getCustomerId());
        System.out.println("Name: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Mobile: " + customer.getMobile());

        System.out.println("\nAccount Details:");

        for (Account account : accounts) {
            System.out.println(
                account.getAccountNumber() +
                " | " +
                account.getOwnerName() +
                " | Balance: ₹" +
                account.getBalance()
            );
        }

        int choice;

        do {
            System.out.println("\n1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            choice = sc.nextInt();

            switch (choice) {
                case 1 -> System.out.println("Open Account - to be implemented in a later lab");
                case 2 -> System.out.println("Deposit - to be implemented in a later lab");
                case 3 -> System.out.println("Withdraw - to be implemented in a later lab");
                case 4 -> System.out.println("Transfer - to be implemented in a later lab");
                case 5 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice");
            }

        } while (choice != 5);

        sc.close();
    }
}