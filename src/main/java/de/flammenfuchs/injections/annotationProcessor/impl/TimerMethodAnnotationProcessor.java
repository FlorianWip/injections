package de.flammenfuchs.injections.annotationProcessor.impl;

import de.flammenfuchs.injections.annon.Timer;
import de.flammenfuchs.injections.annotationProcessor.MethodAnnotationProcessor;
import de.flammenfuchs.javalib.logging.Logger;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.TimerTask;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public class TimerMethodAnnotationProcessor implements MethodAnnotationProcessor {

    private final BiConsumer<Method, Object> methodInvoker;
    private final Logger logger;

    @Override
    public void processMethod(Method method, Object instance) {
        java.util.Timer timer = new java.util.Timer();
        Timer timerAnnotation = method.getAnnotation(Timer.class);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    methodInvoker.accept(method, instance);
                } catch (Exception e) {
                    logger.err("Error while invoking timer method");
                    e.printStackTrace();
                }
            }
        };
        if (timerAnnotation.period() <= 0) {
            timer.schedule(task, timerAnnotation.delay());
        } else {
            timer.schedule(task, timerAnnotation.delay(), timerAnnotation.period());
        }

    }
}
