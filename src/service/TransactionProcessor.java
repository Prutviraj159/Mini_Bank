package service;

import model.Account;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manages multithreaded transaction execution, thread pool, and producer-consumer queue.
 * Introduced in Practical 10 (Part B) and Practical 12 (ConcurrentHashMap).
 */
public class TransactionProcessor {

    private final ExecutorService executorService;
    private final ConcurrentHashMap<String, Account> sharedAccounts;

    // Producer-Consumer Buffer (Practical 10 Requirement 3)
    private final Queue<Runnable> transactionBuffer = new LinkedList<>();
    private final int bufferCapacity;

    public TransactionProcessor() {
        this(4, 20);
    }

    public TransactionProcessor(int poolSize, int bufferCapacity) {
        this.executorService = Executors.newFixedThreadPool(poolSize);
        this.bufferCapacity = bufferCapacity;
        this.sharedAccounts = new ConcurrentHashMap<>();
    }

    public void submit(Runnable task) {
        executorService.execute(task);
    }

    public void stop() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public ConcurrentHashMap<String, Account> getSharedAccounts() {
        return sharedAccounts;
    }

    public void registerSharedAccount(Account account) {
        if (account != null) {
            sharedAccounts.put(account.getAccountNumber(), account);
        }
    }

    // --- Producer - Consumer Coordination using wait() and notify() ---

    public synchronized void produceTransaction(Runnable task) throws InterruptedException {
        while (transactionBuffer.size() >= bufferCapacity) {
            wait();
        }
        transactionBuffer.add(task);
        notifyAll();
    }

    public synchronized Runnable consumeTransaction() throws InterruptedException {
        while (transactionBuffer.isEmpty()) {
            wait();
        }
        Runnable task = transactionBuffer.poll();
        notifyAll();
        return task;
    }

    public synchronized int getPendingCount() {
        return transactionBuffer.size();
    }
}
