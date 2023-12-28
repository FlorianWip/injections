package de.flammenfuchs.injections.discovery;

import de.flammenfuchs.injections.annon.AllowParameters;
import de.flammenfuchs.injections.annotationProcessor.ClassAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.FieldAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.MethodAnnotationProcessor;
import de.flammenfuchs.injections.manager.InjectionsBuilder;
import de.flammenfuchs.injections.registry.AnnotationRegistry;
import de.flammenfuchs.javalib.logging.LogLevel;
import de.flammenfuchs.javalib.logging.Logger;
import de.flammenfuchs.javalib.reflect.ReflectionUtil;
import de.flammenfuchs.javalib.reflect.scanner.ClassScanner;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to discover all classes, fields and methods to be processed by an Annotation
 */
@RequiredArgsConstructor
public class InjectionsDiscovery {

    private final AnnotationRegistry annotationRegistry;
    private final InjectionsBuilder.ClassScannerSupplier supplier;
    private final Logger logger;

    /**
     * Start discovery for a target
     *
     * @param classLoader the {@link ClassLoader} to search in
     * @param topPackage the top package
     * @param ignoredPackages all packages to be ignored
     * @return A {@link DiscoveryResult} which contains all discovered values
     */
    public DiscoveryResult discoverTargets(ClassLoader classLoader, String topPackage, String[] ignoredPackages) {
        ClassScanner scanner = supplier.supply(classLoader, topPackage, ignoredPackages);
        List<Class<?>> classes = filterClasses(scanner.scan(), classLoader);
        Map<Field, FieldAnnotationProcessor> fields = filterFields(classes);
        Map<Method, MethodAnnotationProcessor> methods = filterMethods(classes);

        return new DiscoveryResult(classes, fields, methods);
    }

    /**
     * Filter all annotated classes
     *
     * @param classes {@link List} with all classes to be filtered
     * @param classLoader the {@link ClassLoader} of the classes to be filtered
     * @return a new {@link List} only containing the filtered classes
     */
    @SneakyThrows
    private List<Class<?>> filterClasses(List<Class<?>> classes, ClassLoader classLoader) {
        final List<Class<?>> filtered = new ArrayList<>();
        for (var clazz : classes) {
            for (Annotation annotation : clazz.getAnnotations()) {
                ClassAnnotationProcessor processor = annotationRegistry.getClassAnnotationProcessor(annotation.annotationType());
                if (processor == null) {
                    continue;
                }
                if (processor.processClass(clazz)) {
                    if (isClassValid(clazz)) {
                        this.logger.info(LogLevel.EXTENDED, "Discovered " + clazz.getName());
                        filtered.add(classLoader.loadClass(clazz.getName()));
                    }
                }
                break;
            }
        }
        return filtered;
    }

    private boolean isClassValid(Class<?> clazz) {
        if (!ReflectionUtil.hasEmptyConstructor(clazz)) {
            logger.warn("Missing empty constructor for " + clazz.getName() + ". Skipped this class.");
            return false;
        }
        return true;
    }

    /**
     * Filter all fields
     *
     * @param classes A {@link List} with all classes to search in
     * @return A {@link Map} with the filtered {@link Field} as key and the corresponding processor as value
     */
    private Map<Field, FieldAnnotationProcessor> filterFields(List<Class<?>> classes) {
        final Map<Field, FieldAnnotationProcessor> filtered = new HashMap<>();
        for (var clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                for (Annotation annotation : field.getAnnotations()) {
                    FieldAnnotationProcessor processor = annotationRegistry.getFieldAnnotationProcessor(annotation.annotationType());
                    if (processor == null) {
                        continue;
                    }
                    this.logger.info(LogLevel.EXTENDED, "Discovered " + clazz.getName() + "." + field.getName());
                    filtered.put(field, processor);
                    break;
                }
            }
        }
        return filtered;
    }

    /**
     * Filter all methods
     *
     * @param classes A {@link List} with all classes to search in
     * @return A {@link Map} with the filtered {@link Method} as key and the corresponding processor as value
     */
    private Map<Method, MethodAnnotationProcessor> filterMethods(List<Class<?>> classes) {
        final Map<Method, MethodAnnotationProcessor> filtered = new HashMap<>();
        for (var clazz : classes) {
            for (Method method : clazz.getDeclaredMethods()) {
                for (Annotation annotation : method.getAnnotations()) {
                    MethodAnnotationProcessor processor = annotationRegistry.getMethodAnnotationProcessor(annotation.annotationType());
                    if (processor == null) {
                        continue;
                    }
                    if (method.getParameterCount() > 0
                            && !annotation.annotationType().isAnnotationPresent(AllowParameters.class)) {
                        logger.warn(LogLevel.BASIC, "Cannot process method " + method.getName() + "() in class " +
                                clazz.getName() +
                                ". Methods with @" + annotation.annotationType().getSimpleName() + " can't have parameters.");
                        break;
                    }
                    this.logger.info(LogLevel.EXTENDED, "Discovered " + clazz.getName() + "." + method.getName() + "()");
                    filtered.put(method, processor);
                    break;
                }
            }
        }
        return filtered;
    }

}