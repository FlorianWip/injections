

package de.flammenfuchs.injections.registry;

import de.flammenfuchs.injections.annotationProcessor.ClassAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.FieldAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.MethodAnnotationProcessor;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.*;

/**
 * Registry to register Annotations with their processor
 */
public class AnnotationRegistry {
    private final Map<String, ClassAnnotationProcessor> classAnnotationProcessors = new HashMap<>();
    private final Map<String, FieldAnnotationProcessor> fieldAnnotationProcessors = new HashMap<>();
    private final Map<String, MethodAnnotationProcessor> methodAnnotationProcessors = new HashMap<>();
    private final Map<String, MethodAnnotationProcessor> lateMethodAnnotationProcessors = new HashMap<>();

    /**
     * Register a class annotation
     *
     * @param annonClass the class of the annotation
     * @param annotationProcessor the {@link ClassAnnotationProcessor} for the annotation
     */
    public void registerClassAnnotation(@NonNull Class<? extends Annotation> annonClass,
                                        @NonNull ClassAnnotationProcessor annotationProcessor) {
        checkElementTypeCompatibility(annonClass, ElementType.TYPE);

        this.classAnnotationProcessors.put(annonClass.getName(), annotationProcessor);
    }

    /**
     * Register a field annotation
     *
     * @param annonClass the class of the annotation
     * @param annotationProcessor the {@link FieldAnnotationProcessor} for the annotation
     */
    public void registerFieldAnnotation(@NonNull Class<? extends Annotation> annonClass,
                                        @NonNull FieldAnnotationProcessor annotationProcessor) {

        checkElementTypeCompatibility(annonClass, ElementType.FIELD);

        this.fieldAnnotationProcessors.put(annonClass.getName(), annotationProcessor);
    }
    /**
     * Register a method annotation
     *
     * @param annonClass the class of the annotation
     * @param annotationProcessor the {@link MethodAnnotationProcessor} for the annotation
     */
    public void registerMethodAnnotation(@NonNull Class<? extends Annotation> annonClass,
                                         @NonNull MethodAnnotationProcessor annotationProcessor) {

        checkElementTypeCompatibility(annonClass, ElementType.METHOD);

        this.methodAnnotationProcessors.put(annonClass.getName(), annotationProcessor);
    }

    /**
     * Register a late method annotation
     *
     * @param annonClass the class of the annotation
     * @param annotationProcessor the {@link MethodAnnotationProcessor} for the annotation
     */
    public void registerLateMethodAnnotation(@NonNull Class<? extends Annotation> annonClass,
                                         @NonNull MethodAnnotationProcessor annotationProcessor) {

        checkElementTypeCompatibility(annonClass, ElementType.METHOD);

        this.lateMethodAnnotationProcessors.put(annonClass.getName(), annotationProcessor);
    }

    /**
     * Check if the given annotation is compatible with the given ElementType
     *
     * @param clazz the annotation
     * @param type the ElementType
     */
    private void checkElementTypeCompatibility(Class<? extends Annotation> clazz, ElementType type) {
        if (!clazz.isAnnotationPresent(Target.class)) {
            throw new IllegalArgumentException("Missing essential Annotation @Target on @" +
                    clazz.getSimpleName());
        }
        for (ElementType included : clazz.getAnnotation(Target.class).value()) {
            if (included.equals(type)) {
                return;
            }
        }
        throw new IllegalArgumentException("Incompatible Annotation used!");
    }

    /**
     * Get the {@link ClassAnnotationProcessor} corresponding to the given annotation
     *
     * @param annonClass the corresponding annotation
     * @return the corresponding {@link ClassAnnotationProcessor}
     */
    public ClassAnnotationProcessor getClassAnnotationProcessor(Class<? extends Annotation> annonClass) {
        return this.classAnnotationProcessors.get(annonClass.getName());
    }

    /**
     * Get the {@link FieldAnnotationProcessor} corresponding to the given annotation
     *
     * @param annonClass the corresponding annotation
     * @return the corresponding {@link FieldAnnotationProcessor}
     */
    public FieldAnnotationProcessor getFieldAnnotationProcessor(Class<? extends Annotation> annonClass) {
        return this.fieldAnnotationProcessors.get(annonClass.getName());
    }

    /**
     * Get the {@link MethodAnnotationProcessor} corresponding to the given annotation
     *
     * @param annonClass the corresponding annotation
     * @return the corresponding {@link MethodAnnotationProcessor}
     */
    public MethodAnnotationProcessor getMethodAnnotationProcessor(Class<? extends Annotation> annonClass) {
        return this.methodAnnotationProcessors.get(annonClass.getName());
    }

    /**
     * Get the {@link MethodAnnotationProcessor} corresponding to the given annotation
     *
     * @param annonClass the corresponding annotation
     * @return the corresponding {@link MethodAnnotationProcessor}
     */
    public MethodAnnotationProcessor getLateMethodAnnotationProcessor(Class<? extends Annotation> annonClass) {
        return this.lateMethodAnnotationProcessors.get(annonClass.getName());
    }
}

