package service;

import model.Account;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Persists and loads account state using ObjectOutputStream and ObjectInputStream.
 * Introduced in Practical 11 (Part B).
 */
public class StatePersister {

    /**
     * Saves an array of Account objects to the specified file using try-with-resources.
     */
    public static void save(Account[] accounts, Path file) throws IOException {
        if (file.getParent() != null && !Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(accounts);
        }
    }

    /**
     * Overload to save a Collection of accounts.
     */
    public static void save(Collection<Account> accounts, Path file) throws IOException {
        save(accounts.toArray(new Account[0]), file);
    }

    /**
     * Loads an array of Account objects from the specified file using try-with-resources.
     */
    public static Account[] load(Path file) throws IOException, ClassNotFoundException {
        if (!Files.exists(file)) {
            return new Account[0];
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            return (Account[]) ois.readObject();
        }
    }
}
