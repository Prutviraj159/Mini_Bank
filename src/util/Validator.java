package util;

import java.util.regex.Pattern;

/**
 * Regex-based input validator for banking data boundaries.
 * Introduced in Practical 4 (Part B).
 */
public class Validator {

    // Indian mobile numbers: 10 digits starting with 6, 7, 8, or 9
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");

    // Standard RFC 5322 compatible email pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Indian PAN card: 5 uppercase letters, 4 digits, 1 uppercase letter
    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");

    // Indian IFSC code: 4 uppercase letters, 0, 6 alphanumeric characters
    private static final Pattern IFSC_PATTERN = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

    // Positive whole amount
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^[1-9][0-9]*$");

    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPan(String pan) {
        return pan != null && PAN_PATTERN.matcher(pan.trim()).matches();
    }

    public static boolean isValidIfsc(String ifsc) {
        return ifsc != null && IFSC_PATTERN.matcher(ifsc.trim()).matches();
    }

    public static boolean isValidAmount(String amountStr) {
        return amountStr != null && AMOUNT_PATTERN.matcher(amountStr.trim()).matches();
    }
}
