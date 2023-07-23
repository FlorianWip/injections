

package de.flammenfuchs.injections.registry;

import de.flammenfuchs.injections.annotationProcessor.ClassAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.FieldAnnotationProcessor;
import de.flammenfuchs.injections.annotationProcessor.MethodAnnotationProcessor;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.*;

public class AnnotationRegistry {

    private final Map<String, ClassAnnotationProcessor> classAnnotationProcessors = new HashMap<>();
    private final Map<String, FieldAnnotationProcessor> fieldAnnotationProcessors = new HashMap<>();
    private final Map<String, MethodAnnotationProcessor> methodAnnotationProcessors = new HashMap<>();

    public void registerClassAnnotation(@NonNull Class<? extends Annotation> annonClass,
                                        @NonNull ClassAnnotationProcessor annotationProcessor) {
        checkElementTypeCompatibility(annonClass, ElementType.TYPE);

        this.classAnnotationProcessors.put(annonClass.getName(), annotationProcessor);
    }

    public void registerFieldAnnotation(@NonNull Class<? extends Annotation> annonClass,
                                        @NonNull FieldAnnotationProcessor annotationProcessor) {

        checkElementTypeCompatibility(annonClass, ElementType.FIELD);

        this.fieldAnnotationProcessors.put(annonClass.getName(), annotationProcessor);
    }

    public void registerMethodAnnotation(@NonNull Class<? extends Annotation> annonClass,
                                         @NonNull MethodAnnotationProcessor annotationProcessor) {

        checkElementTypeCompatibility(annonClass, ElementType.METHOD);

        this.methodAnnotationProcessors.put(annonClass.getName(), annotationProcessor);
    }

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

    public ClassAnnotationProcessor getClassAnnotationProcessor(Class<? extends Annotation> annonClass) {
        return this.classAnnotationProcessors.get(annonClass.getName());
    }

    public FieldAnnotationProcessor getFieldAnnotationProcessor(Class<? extends Annotation> annonClass) {
        return this.fieldAnnotationProcessors.get(annonClass.getName());
    }

    public MethodAnnotationProcessor getMethodAnnotationProcessor(Class<? extends Annotation> annonClass) {
        return this.methodAnnotationProcessors.get(annonClass.getName());
    }
}

