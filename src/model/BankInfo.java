package model;

import java.io.Serializable;

/**
 * Read-only bank header record.
 * Introduced in Practical 1 (Part B), Serializable in Practical 11.
 */
public record BankInfo(String name, String branch) implements Serializable {
    private static final long serialVersionUID = 1L;
}
