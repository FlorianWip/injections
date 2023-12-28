package de.flammenfuchs.injections.annotationProcessor;

import java.lang.reflect.Method;

/**
 * A processor for a {@link java.lang.annotation.Annotation} for a {@link Method}
 */
public interface MethodAnnotationProcessor {

    /**
     * Process a method
     *
     * @param method the {@link Method} to be processed
     * @param instance an instance containing the {@link Method}
     */
    void processMethod(Method method, Object instance);
}
