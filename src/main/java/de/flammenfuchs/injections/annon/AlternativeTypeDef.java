package de.flammenfuchs.injections.annon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class annotation to define a method to get alternative types
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AlternativeTypeDef {

    /**
     * The name of the method to get the alternative type with default: value()
     *
     * @return the name of the method
     */
    String value() default "value";
}
