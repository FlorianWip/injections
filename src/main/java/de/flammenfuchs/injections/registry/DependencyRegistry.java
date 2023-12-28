package de.flammenfuchs.injections.registry;

import java.util.*;

/**
 * Registry to register all dependencies and all discovered objects
 */
public class DependencyRegistry {

    private final Map<Class<?>, Object> container = new HashMap<>();

    /**
     * Resolve a given type
     *
     * @param clazz the type to be resolved
     * @return an instance of this type or null if not found
     * @param <T> type of clazz
     */
    public <T> T resolve(Class<T> clazz) {
        return clazz.cast(container.get(clazz));
    }

    /**
     * Register a object
     *
     * @param t the object to be registered
     * @param <T> type of the object
     */
    public <T> void register(T t) {
        this.container.put(t.getClass(), t);
    }

    /**
     * Register a object
     *
     * @param t instance of the object
     * @param clazz the class as which the object has to be registered
     * @param <T> the type of the object and the clazz
     */
    public <T> void register(T t, Class<T> clazz) {
        this.container.put(clazz, t);
    }

    /**
     * Get all registered instances
     *
     * @return An unmodifable Map with the type as key and the corresponding instance as value
     */
    public Map<Class<?>, Object> asMap() {
        return Collections.unmodifiableMap(container);
    }

    /**
     * Get the size of the registry
     *
     * @return amount of how many types are known
     */
    public int getSize() {
        return this.container.size();
    }

    /**
     * Get a {@link Collection} with all values
     *
     * @return A {@link Collection} with all values (maybe duplicates (if instance is registered as different type))
     */
    public Collection<Object> resolveAll() {
        return Collections.unmodifiableCollection(this.container.values());
    }

    /**
     * Get all known types
     *
     * @return A {@link Set} with all types
     */
    public Set<Class<?>> resolveAllTypes() {
        return Collections.unmodifiableSet(this.container.keySet());
    }

}
