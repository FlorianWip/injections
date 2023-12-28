package de.flammenfuchs.injections.annon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link de.flammenfuchs.injections.annotationProcessor.AnnotationProcessorHandler} will try to resolve all fields
 * with this annotation. If no instance could be found, the field value is set to null
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Inject {
}
