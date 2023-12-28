package de.flammenfuchs.injections.registry;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

/**
 * A Consumer which is fed with all discovered objects with the specified type
 *
 * @param <T> the type of the consumed objects
 */
@RequiredArgsConstructor
public class TypeConsumer<T> {

    private final Class<T> clazz;
    private final Consumer<T> consumer;

    /**
     * Consume a object
     *
     * @param object the object to be consumed
     */
    public void consume(Object object) {
        if (clazz.isInstance(object)) {
            this.consumer.accept(clazz.cast(object));
        }
    }
}
