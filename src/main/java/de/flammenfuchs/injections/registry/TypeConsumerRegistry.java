package de.flammenfuchs.injections.registry;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry to register a {@link TypeConsumer}
 */
public class TypeConsumerRegistry {

    private final List<TypeConsumer<?>> registered = new ArrayList<>();

    /**
     * Register a {@link TypeConsumer}
     *
     * @param typeConsumer the consumer to register
     * @param <T> type which is consumed
     */
    public <T> void register(@NonNull TypeConsumer<?> typeConsumer) {
        this.registered.add(typeConsumer);
    }

    /**
     * Forward an object to all TypeConsumers
     *
     * @param object the object to be forwarded
     */
    public void consume(Object object) {
        this.registered.forEach(typeConsumer -> {
            typeConsumer.consume(object);
        });
    }
}
