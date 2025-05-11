package de.flammenfuchs.injections.discovery;

import de.flammenfuchs.injections.annotationProcessor.FieldAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.MethodAnnotationProcessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * A class which contains all discovered data of a discovery of {@link InjectionsDiscovery}
 */
@RequiredArgsConstructor
@Getter
public class DiscoveryResult {

    private final List<Class<?>> classes;
    private final Map<Field, FieldAnnotationProcessor> fields;
    private final Map<Method, MethodAnnotationProcessor> methods;
    private final Map<Method, MethodAnnotationProcessor> lateMethods;

    /**
     * Get the amount of all found classes
     *
     * @return the amount
     */
    public int getClassesFound() {
        return classes.size();
    }

    /**
     * Get the amount of all found fields
     *
     * @return the amount
     */
    public int getFieldsFound() {
        return fields.size();
    }

    /**
     * Get the amount of all found methods
     *
     * @return the amount
     */
    public int getMethodsFound() {
        return methods.size();
    }

    /**
     * Get the amount of all found late methods
     *
     * @return the amount
     */
    public int getLateMethodsFound() {
        return lateMethods.size();
    }
}
