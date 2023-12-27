package de.flammenfuchs.injections.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DependencyRegistry {

    private final Map<Class<?>, Object> container = new HashMap<>();

    public <T> T resolve(Class<T> clazz) {
        return clazz.cast(container.get(clazz));
    }

    public <T> void register(T t) {
        this.container.put(t.getClass(), t);
    }

    public <T> void register(T t, Class<T> clazz) {
        this.container.put(clazz, t);
    }

    public Map<Class<?>, Object> asMap() {
        return Collections.unmodifiableMap(container);
    }

}
