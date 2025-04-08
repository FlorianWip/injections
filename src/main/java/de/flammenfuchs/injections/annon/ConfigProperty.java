package de.flammenfuchs.injections.annon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields annotated with @ConfigProperty are injected with values from a local configuration file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface ConfigProperty {

    /**
     * The key of the property to be injected.
     *
     * @return the key of the property
     */
    String value();

    /**
     * Should the value be saved to the configuration file on shutdown?
     *
     * @return true if the value should be saved, false otherwise
     */
    boolean save() default false;
}
