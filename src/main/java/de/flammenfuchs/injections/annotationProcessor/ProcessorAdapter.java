package de.flammenfuchs.injections.annotationProcessor;

import de.flammenfuchs.injections.annon.AllowParameters;
import de.flammenfuchs.injections.annon.Inject;
import de.flammenfuchs.injections.annon.Instantiate;
import de.flammenfuchs.injections.annon.Invoke;
import de.flammenfuchs.injections.bootstrap.InjectionsBootstrap;
import de.flammenfuchs.injections.logging.LogLevel;
import de.flammenfuchs.injections.logging.Logger;
import de.flammenfuchs.injections.registry.AnnotationRegistry;
import de.flammenfuchs.injections.registry.TypeConsumerRegistry;
import de.flammenfuchs.javalib.reflect.ReflectionUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class ProcessorAdapter {

    private final InjectionsBootstrap bootstrap;
    private final AnnotationRegistry annotationRegistry;
    private final TypeConsumerRegistry typeConsumerRegistry;
    private final Logger logger;

    @Getter
    private final Map<Class<?>, Object> injectable = new HashMap<>();

    private final List<Object> toConsume = new ArrayList<>();

    private final List<Class<?>> classes = new ArrayList<>();
    private final Map<Field, FieldAnnotationProcessor> fields = new HashMap<>();
    private final Map<Method, MethodAnnotationProcessor> methods = new HashMap<>();

    public int[] scan(List<? extends Class<?>> input) {
        logger.info(LogLevel.EXTENDED, "Filter through " + input.size() + " Classes");
        filterClasses(input);
        filterFields();
        filterMethods();
        return new int[]{classes.size(), fields.size(), methods.size()};
    }

    public void registerDefaults() {
        this.annotationRegistry.registerClassAnnotation(Instantiate.class, clazz -> true);
        this.annotationRegistry.registerFieldAnnotation(Inject.class, (field, instance) ->
                this.injectable.get(field.getType()));
        this.annotationRegistry.registerMethodAnnotation(Invoke.class, this::invokeMethod);
        this.injectable(this.annotationRegistry, AnnotationRegistry.class);
        this.injectable(this.typeConsumerRegistry, TypeConsumerRegistry.class);
    }

    @SneakyThrows
    public void process() {
        for (var clazz : classes) {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            this.logger.info(LogLevel.EXTENDED, "Process " + clazz.getName());
            this.injectable.put(clazz, instance);
            this.toConsume.add(instance);
        }
        for (Field field : fields.keySet()) {
            Object owner = this.injectable.get(field.getDeclaringClass());
            FieldAnnotationProcessor processor = this.fields.get(field);
            field.setAccessible(true);
            field.set(owner, processor.processField(field, owner));
            this.logger.info(LogLevel.EXTENDED, "Process " + field.getName() + " in "
                    + field.getDeclaringClass().getName());
        }
        for (Method method : methods.keySet()) {
            Object owner = this.injectable.get(method.getDeclaringClass());
            MethodAnnotationProcessor processor = this.methods.get(method);
            this.logger.info(LogLevel.EXTENDED, "Process " + method.getName() + "() in "
                    + method.getDeclaringClass().getName());
            processor.processMethod(method, owner);
        }
        this.toConsume.forEach(typeConsumerRegistry::consume);
        this.toConsume.clear();
    }

    @SneakyThrows
    private void filterClasses(List<? extends Class<?>> input) {
        for (var clazz : input) {
            if (bootstrap.processAllClasses()) {
                validateClass(clazz);
                continue;
            }
            for (Annotation annotation : clazz.getAnnotations()) {
                ClassAnnotationProcessor processor = annotationRegistry.getClassAnnotationProcessor(annotation.annotationType());
                if (processor == null) {
                    continue;
                }
                if (processor.processClass(clazz)) {
                    validateClass(clazz);
                }
                break;
            }
        }
    }

    public void filterFields() {
        for (var clazz : classes) {
            for (Field field : clazz.getDeclaredFields()) {
                for (Annotation annotation : field.getAnnotations()) {
                    FieldAnnotationProcessor processor = annotationRegistry.getFieldAnnotationProcessor(annotation.annotationType());
                    if (processor == null) {
                        continue;
                    }
                    this.fields.put(field, processor);
                    break;
                }
            }
        }
    }

    public void filterMethods() {
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
                    this.methods.put(method, processor);
                    break;
                }
            }
        }
    }

    public void addInjectable(Object instance) {
        addInjectable(instance, instance.getClass());
    }

    public <T> void addInjectable(T instance, Class<? extends T> type) {
        if (bootstrap.allowExternalInjectable()) {
            injectable(instance, type);
        }
    }

    private <T> void injectable(T instance, Class<? extends T> type) {
        this.injectable.put(type, instance);
    }

    public <T> T getInjectable(Class<T> type) {
        return type.cast(this.injectable.get(type));
    }

    public Map<Class<?>, Object> getInjectables() {
        return Collections.unmodifiableMap(injectable);
    }


    private void validateClass(Class<?> clazz) throws ClassNotFoundException {
        if (!ReflectionUtil.hasEmptyConstructor(clazz)) {
            logger.warn(LogLevel.BASIC, "Cannot instantiate class " + clazz.getName() + ". " +
                    "Class needs empty constructor ");
        } else {
            //TODO atm very hacky way to change the classLoader
            Class<?> load;
            if (bootstrap.classLoader().isPresent()) {
                load = bootstrap.classLoader().get().loadClass(clazz.getName());
            } else {
                load = clazz;
            }
            this.classes.add(load);
        }
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
            params[i] = injectable.get(method.getParameterTypes()[i]);
        }
        method.invoke(instance, params);
    }
}
