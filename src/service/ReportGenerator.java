package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Walks transaction log files using NIO, sums totals, reads attributes, and writes a report.
 * Introduced in Practical 11 (Part B).
 */
public class ReportGenerator {

    // Regex to match "DEPOSIT <accNum> <amount>" or "WITHDRAW <accNum> <amount>"
    private static final Pattern TRANSACTION_PATTERN = Pattern.compile("(?i)(DEPOSIT|WITHDRAW)\\s+(?:from=)?([A-Za-z0-9]+)\\s+(?:to=[A-Za-z0-9]+\\s+)?(?:amount=)?(\\d+)");

    /**
     * Walks logsFolder with Files.newDirectoryStream, sums deposits & withdrawals,
     * reads file attributes, and writes the summary to reportFile.
     */
    public static String generateReport(Path logsFolder, Path reportFile) throws IOException {
        if (!Files.exists(logsFolder)) {
            Files.createDirectories(logsFolder);
        }

        long totalDeposits = 0;
        long totalWithdrawals = 0;
        int fileCount = 0;
        long totalBytesScanned = 0;

        StringBuilder details = new StringBuilder();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsFolder)) {
            for (Path entry : stream) {
                // Supplementary: skip files not ending in .log or empty
                if (!entry.getFileName().toString().endsWith(".log") || Files.size(entry) == 0) {
                    continue;
                }

                fileCount++;
                BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                totalBytesScanned += attrs.size();

                details.append(String.format("  - File: %-25s | Size: %6d bytes | Last Modified: %s\n",
                        entry.getFileName(), attrs.size(), attrs.lastModifiedTime()));

                try (BufferedReader reader = Files.newBufferedReader(entry, StandardCharsets.UTF_8)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Matcher matcher = TRANSACTION_PATTERN.matcher(line);
                        if (matcher.find()) {
                            String type = matcher.group(1);
                            long amount = Long.parseLong(matcher.group(2));
                            if ("DEPOSIT".equalsIgnoreCase(type)) {
                                totalDeposits += amount;
                            } else if ("WITHDRAW".equalsIgnoreCase(type)) {
                                totalWithdrawals += amount;
                            }
                        }
                    }
                }
            }
        }

        long netChange = totalDeposits - totalWithdrawals;

        StringBuilder report = new StringBuilder();
        String border = "================================================================";
        String divider = "----------------------------------------------------------------";

        report.append(border).append("\n");
        report.append("           MINIBANK END-OF-DAY AUDIT SUMMARY REPORT             \n");
        report.append("Generated on    : ").append(LocalDateTime.now()).append("\n");
        report.append("Log Files Read  : ").append(fileCount).append(" file(s)\n");
        report.append("Total Bytes     : ").append(totalBytesScanned).append(" bytes\n");
        report.append(divider).append("\n");
        report.append("Files Processed:\n");
        if (details.length() > 0) {
            report.append(details);
        } else {
            report.append("  (No .log files found with data)\n");
        }
        report.append(divider).append("\n");
        report.append(String.format("Total Deposits  : Rs. %,d\n", totalDeposits));
        report.append(String.format("Total Withdraws : Rs. %,d\n", totalWithdrawals));
        report.append(String.format("Net Cash Flow   : Rs. %,d (%s)\n",
                netChange, netChange >= 0 ? "SURPLUS" : "DEFICIT"));
        report.append(border).append("\n");

        String reportContent = report.toString();

        if (reportFile.getParent() != null && !Files.exists(reportFile.getParent())) {
            Files.createDirectories(reportFile.getParent());
        }

        Files.write(
            reportFile,
            Collections.singletonList(reportContent),
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        );

        return reportContent;
    }
}
