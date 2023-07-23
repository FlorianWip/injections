package de.flammenfuchs.injections.annotationProcessor;

import java.lang.reflect.Field;

public interface FieldAnnotationProcessor {

    Object processField(Field field, Object instance);
}
