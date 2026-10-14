package util;

import model.annotation.MaxLength;
import model.annotation.Positive;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Reflection-based runtime validator for custom annotations (@Positive, @MaxLength).
 * Introduced in Practical 7 (Part B).
 */
public class AnnotationValidator {

    /**
     * Inspects obj and its superclasses for @Positive and @MaxLength annotations,
     * returning validation error messages as a String[].
     */
    public static String[] validate(Object obj) {
        if (obj == null) {
            return new String[]{"Object to validate cannot be null"};
        }

        List<String> errors = new ArrayList<>();
        Class<?> currentClass = obj.getClass();

        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);

                    // Check @Positive annotation
                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive positive = field.getAnnotation(Positive.class);
                        if (value instanceof Number num) {
                            if (num.doubleValue() <= 0) {
                                errors.add("Field '" + field.getName() + "' with value " + num + " violates rule: " + positive.message());
                            }
                        }
                    }

                    // Check @MaxLength annotation
                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength max = field.getAnnotation(MaxLength.class);
                        if (value instanceof String str) {
                            if (str != null && str.length() > max.value()) {
                                errors.add("Field '" + field.getName() + "' length (" + str.length() + ") exceeds maximum allowed (" + max.value() + ")");
                            }
                        }
                    }

                } catch (IllegalAccessException e) {
                    errors.add("Unable to inspect field " + field.getName() + ": " + e.getMessage());
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}
