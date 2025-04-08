package de.flammenfuchs.injections.annon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods annotated with @Timer are invoked as a java timer.<br>
 * These methods are allowed to have parameters. The {@link de.flammenfuchs.injections.annotationProcessor.AnnotationProcessorHandler}
 * will try to resolve it from its known dependencies. When a parameter value is not found, it will be null.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AllowParameters
public @interface Timer {

    /**
     * Sets the delay after which the method will be invoked.
     *
     * @return the delay in milliseconds
     */
    long delay();

    /**
     * Sets the period at which the method will be repeated.
     * A value below 0 will deactivate repeating.
     *
     * @return the period in milliseconds
     */
    long period() default -1;
}
