package de.flammenfuchs.injections.annotationProcessor;

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
        for (Field field : fields.keySet()) {
            Object owner = this.dependencyRegistry.resolve(field.getDeclaringClass());
            FieldAnnotationProcessor processor = this.fields.get(field);
            field.setAccessible(true);
            field.set(owner, processor.processField(field, owner));
            this.logger.info(LogLevel.EXTENDED, "Process " + field.getName() + " in "
                    + field.getDeclaringClass().getName());
        }
        for (Method method : methods.keySet()) {
            Object owner = this.dependencyRegistry.resolve(method.getDeclaringClass());
            MethodAnnotationProcessor processor = this.methods.get(method);
            this.logger.info(LogLevel.EXTENDED, "Process " + method.getName() + "() in "
                    + method.getDeclaringClass().getName());
            processor.processMethod(method, owner);
        }
        this.toConsume.forEach(typeConsumerRegistry::consume);
        this.toConsume.clear();
    }

}
