package de.flammenfuchs.injections;

import de.flammenfuchs.injections.bootstrap.InjectionsBootstrap;
import de.flammenfuchs.injections.manager.legacy.InjectionsManager;
import de.flammenfuchs.injections.sample.TestA;
import de.flammenfuchs.injections.sample.TestB;
import de.flammenfuchs.javalib.logging.LogLevel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnnotationProcessorTest {

    static InjectionsManager manager;

    @BeforeAll
    static void initTest() {
        manager = new InjectionsManager(
                InjectionsBootstrap.create("de.flammenfuchs.injections")
                        .logLevel(LogLevel.EXTENDED)
        );
        manager.start();
    }

    @Test
    public void testInstantiate() {
        int entries = 0;
        for (var clazz : manager.getAdapter().getInjectables().keySet()) {
            if (clazz.getName().startsWith("de.flammenfuchs.injections.sample")) {
                entries++;
            }
        }
        assertEquals(2, entries);
    }

    @Test
    public void testInject() {
        TestA test = manager.getAdapter().getInjectable(TestA.class);
        assertNotNull(test.a);
    }

    @Test
    public void testInvoke() {
        TestB test = manager.getAdapter().getInjectable(TestB.class);
        assertEquals(7, test.a);
    }

}