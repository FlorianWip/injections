package de.flammenfuchs.injections;

import de.flammenfuchs.injections.manager.InjectionsBuilder;
import de.flammenfuchs.injections.manager.InjectionsManager;
import de.flammenfuchs.injections.sample.TestA;
import de.flammenfuchs.injections.sample.TestB;
import de.flammenfuchs.injections.sample.TestD;
import de.flammenfuchs.javalib.logging.LogLevel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnnotationProcessorTest {

    static InjectionsManager manager;

    @BeforeAll
    void initTest() {
        manager = InjectionsBuilder.create().setLoggerWithLogLevel(LogLevel.EXTENDED).addTarget(this).build();
        manager.start();
    }

    @Test
    public void testInstantiate() {
        int entries = 0;
        for (var clazz : manager.getDependencyRegistry().resolveAllTypes()) {
            if (clazz.getName().startsWith("de.flammenfuchs.injections.sample")) {
                entries++;
            }
        }
        assertEquals(2, entries);
    }

    @Test
    public void testInject() {
        TestA test = manager.getDependencyRegistry().resolve(TestA.class);
        assertNotNull(test.a);
    }

    @Test
    public void testInvoke() {
        TestB test = manager.getDependencyRegistry().resolve(TestB.class);
        assertEquals(7, test.a);
    }

    @Test
    public void testObjectInject() {
        TestD test = new TestD();
        manager.processObject(test);
        assertEquals(7, test.a.a.a);
    }

    @Test
    public void testObjectInvoke() {
        TestD test = new TestD();
        manager.processObject(test);
        assertEquals(7, test.b);
    }
}