package de.flammenfuchs.injections.registry;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class TypeConsumer<T> {

    private final Class<T> clazz;
    private final Consumer<T> consumer;

    public void consume(Object object) {
        if (clazz.isInstance(object)) {
            this.consumer.accept(clazz.cast(object));
        }
    }
}
