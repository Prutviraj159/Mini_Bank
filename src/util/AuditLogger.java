package util;

import model.annotation.Audit;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * Reflective audit logger to discover and log @Audit annotated operations.
 * Introduced in Practical 14 (Part B).
 */
public class AuditLogger {

    public static void inspectAndLog(Class<?> targetClass) {
        System.out.println("=== Reflective Audit Scan on " + targetClass.getSimpleName() + " ===");
        Method[] methods = targetClass.getDeclaredMethods();
        int count = 0;
        for (Method method : methods) {
            if (method.isAnnotationPresent(Audit.class)) {
                Audit audit = method.getAnnotation(Audit.class);
                System.out.printf("  [@Audit Found] Method: %-20s | Action: %s\n",
                        method.getName() + "()", audit.action().isEmpty() ? "DEFAULT" : audit.action());
                count++;
            }
        }
        System.out.println("Total audited operations discovered: " + count);
    }

    public static void logExecution(String action, String details) {
        System.out.printf("[%s] [AUDIT] %s - %s\n", LocalDateTime.now(), action, details);
    }
}
