package de.flammenfuchs.injections.annotationProcessor;

/**
 * A processor for a {@link java.lang.annotation.Annotation} for a {@link Class}
 */
public interface ClassAnnotationProcessor {

    /**
     * Process a class
     *
     * @param clazz the {@link Class} to be processed
     * @return if class should be processed
     */
    boolean processClass(Class clazz);

}
