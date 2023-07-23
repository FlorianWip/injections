package de.flammenfuchs.injections.annotationProcessor;

import java.lang.reflect.Method;

public interface MethodAnnotationProcessor {

    void processMethod(Method method, Object instance);
}
