package de.flammenfuchs.injections.registry;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TypeConsumerRegistry {

    private final List<TypeConsumer<?>> registered = new ArrayList<>();

    public <T> void register(@NonNull TypeConsumer<?> typeConsumer) {
        this.registered.add(typeConsumer);
    }

    public void consume(Object object) {
        this.registered.forEach(typeConsumer -> {
            typeConsumer.consume(object);
        });
    }
}
