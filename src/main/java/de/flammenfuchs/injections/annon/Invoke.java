package de.flammenfuchs.injections.annon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with @Invoke are invoked after construction and injection is finished.<br>
 * These methods are allowed to have parameters. The {@link de.flammenfuchs.injections.annotationProcessor.AnnotationProcessorHandler}
 * will try to resolve it from its known dependencies. When a parameter value is not found, it will be null.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AllowParameters
public @interface Invoke {
}
