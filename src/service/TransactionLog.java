package service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * Append-only audit log for bank transactions.
 * Introduced in Practical 11 (Part B).
 */
public class TransactionLog {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Appends a log line to the specified file using Files.write with CREATE and APPEND.
     */
    public static void append(Path file, String line) throws IOException {
        if (file.getParent() != null && !Files.exists(file.getParent())) {
            Files.createDirectories(file.getParent());
        }
        String entry = "[" + LocalDateTime.now().format(TIMESTAMP_FORMATTER) + "] " + line;
        Files.write(
            file,
            Collections.singletonList(entry),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.APPEND
        );
    }
}
