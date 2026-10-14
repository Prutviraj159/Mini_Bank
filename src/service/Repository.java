package service;

import model.annotation.Id;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Generic reflection-based repository supporting any entity with an @Id annotated field.
 * Introduced in Practical 13 (Part B).
 *
 * @param <T> The entity type stored in this repository
 */
public class Repository<T> {

    // Thread-safe map storing entities by their @Id string key
    private final Map<String, T> storage = new ConcurrentHashMap<>();

    /**
     * Saves an entity, automatically discovering its ID through the @Id annotation via reflection.
     * Practical 13 Requirements 1 & 2.
     */
    public T save(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }

        String id = extractId(entity);
        if (id == null) {
            throw new IllegalArgumentException("Entity of class " + entity.getClass().getSimpleName() + " has no non-null field annotated with @Id");
        }

        storage.put(id, entity);
        return entity;
    }

    /**
     * Saves multiple entities at once using varargs.
     * Practical 13 Requirement 3.
     */
    @SafeVarargs
    public final void saveAll(T... items) {
        if (items != null) {
            for (T item : items) {
                if (item != null) {
                    save(item);
                }
            }
        }
    }

    /**
     * Finds an entity by its string ID.
     */
    public T findById(String id) {
        if (id == null) return null;
        return storage.get(id);
    }

    /**
     * Returns an ArrayList containing all stored entities.
     */
    public ArrayList<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Deletes an entity by ID.
     */
    public boolean delete(String id) {
        if (id == null) return false;
        return storage.remove(id) != null;
    }

    /**
     * Supplementary method: returns count of entities stored.
     */
    public int count() {
        return storage.size();
    }

    /**
     * Generic bounded type parameter method calculating the average of numeric arrays.
     * Practical 13 Requirement 4.
     */
    public static <N extends Number> double average(N[] values) {
        if (values == null || values.length == 0) {
            return 0.0;
        }
        double sum = 0.0;
        for (N val : values) {
            if (val != null) {
                sum += val.doubleValue();
            }
        }
        return sum / values.length;
    }

    /**
     * Helper method using reflection to find the @Id field in an entity's class hierarchy.
     */
    private String extractId(T entity) {
        Class<?> current = entity.getClass();
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    try {
                        Object val = field.get(entity);
                        return val != null ? val.toString() : null;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Cannot access @Id field " + field.getName(), e);
                    }
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }
}
