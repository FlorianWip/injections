package de.flammenfuchs.injections.discovery;

import de.flammenfuchs.injections.annotationProcessor.FieldAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.MethodAnnotationProcessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class DiscoveryResult {

    private final List<Class<?>> classes;
    private final Map<Field, FieldAnnotationProcessor> fields;
    private final Map<Method, MethodAnnotationProcessor> methods;

    public int getClassesFound() {
        return classes.size();
    }

    public int getFieldsFound() {
        return fields.size();
    }

    public int getMethodsFound() {
        return methods.size();
    }
}
