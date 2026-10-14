package util;

import model.Account;
import model.Customer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates formatted multi-line statements using StringBuilder.
 * Introduced in Practical 4 (Part B).
 */
public class StatementFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static String buildStatement(Account account) {
        if (account == null) {
            return "No account specified.";
        }

        StringBuilder sb = new StringBuilder();
        String border = "================================================================";
        String divider = "----------------------------------------------------------------";

        sb.append(border).append("\n");
        sb.append("                  MINI BANK ACCOUNT STATEMENT                   \n");
        sb.append("Generated on: ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
        sb.append(divider).append("\n");
        sb.append(String.format("Account Number   : %s\n", account.getAccountNumber()));
        sb.append(String.format("Account Holder   : %s\n", account.getOwnerName()));
        sb.append(String.format("Account Type     : %s\n", account.getAccountType()));
        sb.append(String.format("Account Status   : %s\n", account.isActive() ? "ACTIVE" : "INACTIVE"));

        Customer cust = account.getCustomer();
        if (cust != null) {
            sb.append(String.format("Customer ID      : %s\n", cust.getCustomerId()));
            sb.append(String.format("Email Address    : %s\n", cust.getEmail()));
            sb.append(String.format("Mobile Number    : %s\n", cust.getMobile()));
            if (cust.getAddress() != null) {
                sb.append(String.format("Address          : %s\n", cust.getAddress()));
            }
        }

        sb.append(divider).append("\n");
        sb.append(String.format("Current Balance  : ₹%,d\n", account.getBalance()));
        sb.append(String.format("Interest Rate    : %.2f%% p.a.\n", account.interestRate()));
        sb.append(String.format("Est. Annual Int. : ₹%,.2f\n", account.yearlyInterest()));
        sb.append(String.format("Est. Monthly Int.: ₹%,.2f\n", account.monthlyInterest()));
        sb.append(border).append("\n");

        return sb.toString();
    }
}
