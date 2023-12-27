package de.flammenfuchs.injections.manager;

import de.flammenfuchs.injections.annon.Inject;
import de.flammenfuchs.injections.annon.Instantiate;
import de.flammenfuchs.injections.annon.Invoke;
import de.flammenfuchs.injections.registry.AnnotationRegistry;
import de.flammenfuchs.injections.registry.DependencyRegistry;
import de.flammenfuchs.injections.registry.TypeConsumerRegistry;
import de.flammenfuchs.javalib.lang.triple.Triple;
import de.flammenfuchs.javalib.logging.LogLevel;
import de.flammenfuchs.javalib.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class InjectionsManager {

    private final List<Triple<ClassLoader, String, String[]>> targets;
    private final boolean defaultAnnotations;
    private final Logger logger;
    private final InjectionsBuilder.ClassScannerSupplier scannerSupplier;

    private final AnnotationRegistry annotationRegistry = new AnnotationRegistry();
    private final TypeConsumerRegistry typeConsumerRegistry = new TypeConsumerRegistry();
    private final DependencyRegistry dependencyRegistry = new DependencyRegistry();

    public void start() {
        this.logger.info("Start processing...");
        if (defaultAnnotations) {
            registerDefaultAnnotations();
        } else {
            this.logger.info(LogLevel.EXTENDED, "Skipped default annotations, because it was disabled.");
        }
        
    }

    private void registerDefaultAnnotations()  {
        this.annotationRegistry.registerClassAnnotation(Instantiate.class, clazz -> true);
        this.annotationRegistry.registerFieldAnnotation(Inject.class, (field, instance) ->
                this.dependencyRegistry.resolve(field.getType()));
        this.annotationRegistry.registerMethodAnnotation(Invoke.class, this::invokeMethod);
        this.dependencyRegistry.register(this.annotationRegistry);
        this.dependencyRegistry.register(this.typeConsumerRegistry);
        this.logger.info("Registered default annotations.");
    }

    @SneakyThrows
    public void invokeMethod(Method method, Object instance) {
        int paramCount = method.getParameterCount();
        method.setAccessible(true);
        if (paramCount == 0) {
            method.invoke(instance);
            return;
        }
        Object[] params = new Object[paramCount];
        for (int i = 0; i < paramCount; i++) {
            params[i] = this.dependencyRegistry.resolve(method.getParameterTypes()[i]);
        }
        method.invoke(instance, params);
    }
}
