package de.flammenfuchs.injections.annotationProcessor.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import de.flammenfuchs.injections.annon.ConfigProperty;
import de.flammenfuchs.injections.annotationProcessor.FieldAnnotationProcessor;
import de.flammenfuchs.javalib.config.v2.FileHandler;
import de.flammenfuchs.javalib.config.v2.JsonFileHandler;
import de.flammenfuchs.javalib.lang.tuple.Tuple;
import de.flammenfuchs.javalib.logging.Logger;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigPropertyFieldAnnotationProcessor implements FieldAnnotationProcessor {

    private final Logger logger;
    private final FileHandler fileHandler;
    private final Wrapper wrapper;

    private final List<Tuple<Field, Object>> toSave = new ArrayList<>();

    public ConfigPropertyFieldAnnotationProcessor(Logger logger, String path, String fileName, List<Tuple<Type, TypeAdapter<?>>> typeAdapters) {
        this.logger = logger;

        GsonBuilder gsonBuilder = new GsonBuilder();
        for (Tuple<Type, TypeAdapter<?>> typeAdapter : typeAdapters) {
            gsonBuilder.registerTypeAdapter(typeAdapter.a(), typeAdapter.b());
        }
        gsonBuilder.setPrettyPrinting().disableHtmlEscaping();


        this.fileHandler = new JsonFileHandler(path, gsonBuilder.create());
        this.wrapper = fileHandler.loadFile(fileName, Wrapper.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Tuple<Field, Object> tuple : toSave) {
                String key = tuple.a().getAnnotation(ConfigProperty.class).value();
                Object instance = tuple.b();
                try {
                    Object value = tuple.a().get(instance);
                    wrapper.properties.put(key, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            fileHandler.saveFile(fileName, wrapper);
            logger.info("Saved config property file");
        }));
    }

    @SneakyThrows
    @Override
    public Object processField(Field field, Object instance) {
        ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
        String key = annotation.value();
        Class<?> type = field.getType();

        if (annotation.save()) {
            toSave.add(Tuple.finalTuple(field, instance));
        }

        Object value;
        if (wrapper.properties.containsKey(key)) {
            Object wrapperValue = wrapper.properties.get(key);
            if (type.isAssignableFrom(wrapperValue.getClass())) {
                value = wrapperValue;
            } else {
                if (isNumber(type) && isNumber(wrapperValue.getClass())) {
                    value = fixNumberValue(type, wrapperValue);
                } else {
                    logger.err("Type mismatch for key: " + key + ". Expected: " + type.getName() + ", but got: " + wrapperValue.getClass().getName() + ". Used already set value.");
                    value = field.get(instance);
                }
            }
        } else {
            value = field.get(instance);
            if (value != null) {
                wrapper.properties.put(key, value);
            }
        }
        return value;
    }

    private boolean isNumber(Class<?> type) {
        return Number.class.isAssignableFrom(type) || type.isPrimitive() && (type.equals(int.class) || type.equals(long.class) || type.equals(float.class) || type.equals(double.class) || type.equals(short.class) || type.equals(byte.class));
    }

    private Object fixNumberValue(Class<?> type, Object value) {
        if (type.equals(int.class) || type.equals(Integer.class)) {
            return ((Number) value).intValue();
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            return ((Number) value).longValue();
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            return ((Number) value).floatValue();
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            return ((Number) value).doubleValue();
        } else if (type.equals(short.class) || type.equals(Short.class)) {
            return ((Number) value).shortValue();
        } else if (type.equals(byte.class) || type.equals(Byte.class)) {
            return ((Number) value).byteValue();
        }
        logger.warn("Tried to fix number value for type: " + type.getName() + ", but failed for value: " + value);
        return null;
    }

    public static class Wrapper {
        Map<String, Object> properties = new HashMap<>();
    }
}
