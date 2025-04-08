package de.flammenfuchs.injections.manager;

import com.google.gson.TypeAdapter;
import de.flammenfuchs.injections.annon.*;
import de.flammenfuchs.injections.annon.Shutdown;
import de.flammenfuchs.injections.annon.Timer;
import de.flammenfuchs.injections.annotationProcessor.AnnotationProcessorHandler;
import de.flammenfuchs.injections.annotationProcessor.FieldAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.MethodAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.impl.ConfigPropertyFieldAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.impl.TimerMethodAnnotationProcessor;
import de.flammenfuchs.injections.discovery.DiscoveryResult;
import de.flammenfuchs.injections.discovery.InjectionsDiscovery;
import de.flammenfuchs.injections.registry.AnnotationRegistry;
import de.flammenfuchs.injections.registry.DependencyRegistry;
import de.flammenfuchs.injections.registry.TypeConsumerRegistry;
import de.flammenfuchs.javalib.lang.triple.Triple;
import de.flammenfuchs.javalib.lang.tuple.Tuple;
import de.flammenfuchs.javalib.logging.LogLevel;
import de.flammenfuchs.javalib.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * This class manages the injection and needs to be instantiated with an {@link InjectionsBuilder}
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class InjectionsManager {

    private final List<Triple<ClassLoader, String, String[]>> targets;
    private final boolean defaultAnnotations;
    private final boolean enableConfigFileInjection;
    private final Tuple<String, String> configFilePath;
    private final List<Tuple<Type, TypeAdapter<?>>> typeAdapters;
    private final Logger logger;
    private final InjectionsBuilder.ClassScannerSupplier scannerSupplier;

    private final AnnotationRegistry annotationRegistry = new AnnotationRegistry();
    private final TypeConsumerRegistry typeConsumerRegistry = new TypeConsumerRegistry();
    private final DependencyRegistry dependencyRegistry = new DependencyRegistry();

    @Getter(AccessLevel.NONE)
    private AnnotationProcessorHandler annotationProcessorHandler;

    /**
     * Start the injection
     */
    public void start() {
        long startAll = System.currentTimeMillis();
        this.logger.info("Start injections...");
        if (defaultAnnotations) {
            registerDefaultAnnotations();
        } else {
            this.logger.info(LogLevel.EXTENDED, "Skipped default annotations, because it was disabled.");
        }

        InjectionsDiscovery discovery = new InjectionsDiscovery(annotationRegistry, scannerSupplier, logger);

        final List<Class<?>> classes = new ArrayList<>();
        final Map<Field, FieldAnnotationProcessor> fields = new HashMap<>();
        final Map<Method, MethodAnnotationProcessor> methods = new HashMap<>();

        this.logger.info("Start discovering all targets...");
        long startDiscovery = System.currentTimeMillis();
        for (int i = 0; i < targets.size(); i++) {
            Triple<ClassLoader, String, String[]> target = targets.get(i);

            this.logger.info(LogLevel.EXTENDED, "Discover target " + (i + 1) + "/" + targets.size());
            DiscoveryResult result = discovery.discoverTargets(target.a(), target.b(), target.c());

            classes.addAll(result.getClasses());
            this.logger.info(LogLevel.EXTENDED, "Discovered " + result.getClassesFound() + " classes in target " + (i + 1));

            fields.putAll(result.getFields());
            this.logger.info(LogLevel.EXTENDED, "Discovered " + result.getFieldsFound() + " fields in target " + (i + 1));

            methods.putAll(result.getMethods());
            this.logger.info(LogLevel.EXTENDED, "Discovered " + result.getMethodsFound() + " methods in target " + (i + 1));
        }
        this.logger.info("Discovered " + classes.size() + " classes in total.");
        this.logger.info("Discovered " + fields.size() + " fields in total.");
        this.logger.info("Discovered " + methods.size() + " methods in total.");
        this.logger.info("Discovery done. Took " + (System.currentTimeMillis() - startDiscovery) + "ms");

        annotationProcessorHandler = new AnnotationProcessorHandler(classes, fields, methods,
                logger, dependencyRegistry, typeConsumerRegistry);
        long startProcessing = System.currentTimeMillis();
        this.logger.info("Start processing...");
        annotationProcessorHandler.handleProcessors();
        this.logger.info("Processing done. Took " + (System.currentTimeMillis() - startProcessing) + "ms");

        this.logger.info("Injections done. Took " + (System.currentTimeMillis() - startAll) + "ms");
    }

    /**
     * Register the default annotations
     */
    private void registerDefaultAnnotations()  {
        this.annotationRegistry.registerClassAnnotation(Scoped.class, clazz -> true);
        this.annotationRegistry.registerFieldAnnotation(Inject.class, (field, instance) ->
                this.dependencyRegistry.resolve(field.getType()));
        this.annotationRegistry.registerMethodAnnotation(Startup.class, this::invokeMethod);
        this.annotationRegistry.registerMethodAnnotation(Shutdown.class, (method, instance) -> {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {this.invokeMethod(method, instance);}));
        });
        this.annotationRegistry.registerMethodAnnotation(Timer.class, new TimerMethodAnnotationProcessor(this::invokeMethod, logger));

        if (enableConfigFileInjection) {
            this.annotationRegistry.registerFieldAnnotation(ConfigProperty.class,
                    new ConfigPropertyFieldAnnotationProcessor(logger, configFilePath.a(), configFilePath.b(), typeAdapters));
        }

        this.dependencyRegistry.register(this.annotationRegistry);
        this.dependencyRegistry.register(this.typeConsumerRegistry);
        this.dependencyRegistry.register(this.dependencyRegistry);
        this.logger.info("Registered default annotations.");
    }

    /**
     * Safely invoke methods
     *
     * @param method the method to be invoked
     * @param instance the object holding the method
     */
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

    /**
     * Process a single object
     *
     * @param object the object to be processed
     */
    public void processObject(Object object) {
        DiscoveryResult result = new InjectionsDiscovery(annotationRegistry, scannerSupplier, logger).discoveryFromObject(object);
        annotationProcessorHandler.handleObject(object, result);
    }
}