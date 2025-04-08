package de.flammenfuchs.injections.annon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * All classes annotated with @Scoped are marked to be processed.<br>
 * These classes need an empty constructor.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scoped {
}
