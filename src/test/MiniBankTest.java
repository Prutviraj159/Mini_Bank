package test;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.*;
import service.AccountWorker;
import service.Repository;
import service.StatePersister;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Test suite for MiniBank fulfilling Practical 14 (Part B) requirements:
 * 1. Deposit increases balance.
 * 2. Withdrawal over limit throws InsufficientFundsException.
 * 3. Concurrent deposits with multithreading keep the balance correct.
 * 4. State serialization save & reload preserves state.
 * 5. Generic Repository correctly stores and fetches by @Id.
 */
public class MiniBankTest {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("          RUNNING MINIBANK TEST SUITE            ");
        System.out.println("=================================================");

        testDepositIncreasesBalance();
        testWithdrawalOverLimitThrowsException();
        testConcurrentDepositsKeepBalanceCorrect();
        testStatePersistenceAndReload();
        testGenericRepositoryWithReflection();
        testInterestCalculation();

        System.out.println("-------------------------------------------------");
        System.out.printf("Test Results: %d Passed, %d Failed.\n", testsPassed, testsFailed);
        System.out.println("=================================================");

        if (testsFailed > 0) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition, String errorMsg) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName + " -> " + errorMsg);
            testsFailed++;
        }
    }

    public static void testDepositIncreasesBalance() {
        try {
            SavingsAccount account = new SavingsAccount("Alice", 1000);
            account.deposit(500);
            assertTrue("testDepositIncreasesBalance", account.getBalance() == 1500,
                    "Expected 1500, got: " + account.getBalance());
        } catch (Exception e) {
            assertTrue("testDepositIncreasesBalance", false, "Unexpected exception: " + e.getMessage());
        }
    }

    public static void testWithdrawalOverLimitThrowsException() {
        boolean exceptionThrown = false;
        long shortfall = 0;
        try {
            // SavingsAccount has minBalance = 500 by default. Opening with 1000 means only 500 can be withdrawn.
            SavingsAccount account = new SavingsAccount("Bob", 1000, 500);
            account.withdraw(600); // 1000 - 600 = 400 < 500 -> must throw
        } catch (InsufficientFundsException e) {
            exceptionThrown = true;
            shortfall = e.getShortfall();
        } catch (Exception e) {
            exceptionThrown = false;
        }

        assertTrue("testWithdrawalOverLimitThrowsException", exceptionThrown && shortfall == 100,
                "Expected InsufficientFundsException with shortfall 100, got shortfall: " + shortfall);
    }

    public static void testConcurrentDepositsKeepBalanceCorrect() {
        try {
            SavingsAccount account = new SavingsAccount("ConcurrencyTester", 0);
            int threadCount = 10;
            int depositsPerThread = 1000;
            long amountPerDeposit = 1;

            Thread[] threads = new Thread[threadCount];
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(new AccountWorker(account, depositsPerThread, amountPerDeposit), "Worker-" + (i + 1));
                threads[i].start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            long expected = threadCount * depositsPerThread * amountPerDeposit;
            assertTrue("testConcurrentDepositsKeepBalanceCorrect", account.getBalance() == expected,
                    "Expected " + expected + ", got " + account.getBalance());
        } catch (Exception e) {
            assertTrue("testConcurrentDepositsKeepBalanceCorrect", false, "Thread error: " + e.getMessage());
        }
    }

    public static void testStatePersistenceAndReload() {
        try {
            Path tempFile = Files.createTempFile("minibank_test_accounts", ".dat");
            Account[] original = new Account[]{
                    new SavingsAccount("User1", 5000),
                    new CurrentAccount("User2", 10000)
            };

            StatePersister.save(original, tempFile);
            Account[] loaded = StatePersister.load(tempFile);

            boolean match = loaded.length == 2 &&
                    loaded[0].getOwnerName().equals("User1") && loaded[0].getBalance() == 5000 &&
                    loaded[1].getOwnerName().equals("User2") && loaded[1].getBalance() == 10000;

            Files.deleteIfExists(tempFile);
            assertTrue("testStatePersistenceAndReload", match, "Loaded data did not match saved data");
        } catch (Exception e) {
            assertTrue("testStatePersistenceAndReload", false, "Persistence error: " + e.getMessage());
        }
    }

    public static void testGenericRepositoryWithReflection() {
        try {
            Repository<Account> repo = new Repository<>();
            SavingsAccount a1 = new SavingsAccount("UserA", 3000);
            CurrentAccount a2 = new CurrentAccount("UserB", 4000);

            repo.saveAll(a1, a2);

            Account found = repo.findById(a1.getAccountNumber());
            ArrayList<Account> all = repo.findAll();

            boolean ok = found != null && found.getOwnerName().equals("UserA") && all.size() == 2;
            assertTrue("testGenericRepositoryWithReflection", ok, "Repository did not return expected entity");
        } catch (Exception e) {
            assertTrue("testGenericRepositoryWithReflection", false, "Repository error: " + e.getMessage());
        }
    }

    public static void testInterestCalculation() {
        SavingsAccount sa = new SavingsAccount("InterestUser", 10000); // 4%
        double yearly = sa.yearlyInterest(); // 400.0
        double monthly = sa.monthlyInterest(); // 400 / 12 = 33.333...

        assertTrue("testInterestCalculation", Math.abs(yearly - 400.0) < 0.01 && Math.abs(monthly - (400.0 / 12)) < 0.01,
                "Interest calculation discrepancy: yearly=" + yearly + ", monthly=" + monthly);
    }
}
