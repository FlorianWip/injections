package de.flammenfuchs.injections.annotationProcessor;

import de.flammenfuchs.injections.discovery.DiscoveryResult;
import de.flammenfuchs.injections.registry.DependencyRegistry;
import de.flammenfuchs.injections.registry.TypeConsumerRegistry;
import de.flammenfuchs.javalib.logging.LogLevel;
import de.flammenfuchs.javalib.logging.Logger;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to handle all processors
 */
@RequiredArgsConstructor
public class AnnotationProcessorHandler {

    private final List<Class<?>> classes;
    private final Map<Field, FieldAnnotationProcessor> fields;
    private final Map<Method, MethodAnnotationProcessor> methods;
    private final Logger logger;
    private final DependencyRegistry dependencyRegistry;
    private final TypeConsumerRegistry typeConsumerRegistry;

    private final List<Object> toConsume = new ArrayList<>();

    /**
     * Handle all processors
     */
    @SneakyThrows
    public void handleProcessors() {
        for (var clazz : classes) {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            this.logger.info(LogLevel.EXTENDED, "Process " + clazz.getName());
            this.dependencyRegistry.register(instance);
            this.toConsume.add(instance);
        }
        this.fields.forEach((field, processor) -> {
            Object owner = this.dependencyRegistry.resolve(field.getDeclaringClass());
            processField(field, processor, owner);
        });
        this.methods.forEach((method, processor) -> {
            Object owner = this.dependencyRegistry.resolve(method.getDeclaringClass());
            processMethod(method, processor, owner);
        });
        this.toConsume.forEach(typeConsumerRegistry::consume);
        this.toConsume.clear();
    }

    /**
     * Handle a single object
     *
     * @param object the object to be handled
     * @param result the result of the discovery
     */
    public void handleObject(Object object, DiscoveryResult result) {
        result.getFields().forEach((field, processor) -> this.processField(field, processor, object));
        result.getMethods().forEach((method, processor) -> this.processMethod(method, processor, object));
        this.typeConsumerRegistry.consume(object);
    }

    /**
     * Process a field
     *
     * @param field the field to be processed
     * @param processor the processor of the field
     * @param owner the owner of the field
     */
    @SneakyThrows
    private void processField(Field field, FieldAnnotationProcessor processor, Object owner) {
        field.setAccessible(true);
        field.set(owner, processor.processField(field, owner));
        this.logger.info(LogLevel.EXTENDED, "Process " + field.getName() + " in "
                + field.getDeclaringClass().getName());
    }

    /**
     * Process a method
     *
     * @param method the method to be processed
     * @param processor the processor of the method
     * @param owner the owner of the method
     */
    @SneakyThrows
    private void processMethod(Method method, MethodAnnotationProcessor processor, Object owner) {
        this.logger.info(LogLevel.EXTENDED, "Process " + method.getName() + "() in "
                + method.getDeclaringClass().getName());
        processor.processMethod(method, owner);
    }

}
