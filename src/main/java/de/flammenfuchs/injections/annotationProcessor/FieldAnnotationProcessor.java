package de.flammenfuchs.injections.annotationProcessor;

import java.lang.reflect.Field;

/**
 * A processor for a {@link java.lang.annotation.Annotation} for a {@link Field}
 */
public interface FieldAnnotationProcessor {

    /**
     * Process a field
     *
     * @param field the {@link Field} to be processed
     * @param instance an instance containing the {@link Field}
     * @return the object which is set as value for the {@link Field} of the instance
     */
    Object processField(Field field, Object instance);
}
