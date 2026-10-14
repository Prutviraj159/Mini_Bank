package util;

/**
 * AutoCloseable banking session for demonstrating try-with-resources.
 * Introduced in Practical 8 (Part B).
 */
public class BankSession implements AutoCloseable {
    private final String sessionId;
    private final String operator;
    private boolean closed;

    public BankSession(String operator) {
        this.operator = operator;
        this.sessionId = "SESS-" + System.currentTimeMillis();
        this.closed = false;
        System.out.println("[SESSION OPENED] Session ID: " + sessionId + " by " + operator);
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public void close() {
        if (!closed) {
            closed = true;
            System.out.println("[SESSION CLOSED] Session ID: " + sessionId + " safely closed.");
        }
    }
}
