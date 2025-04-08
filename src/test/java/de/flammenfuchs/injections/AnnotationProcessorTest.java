package de.flammenfuchs.injections;

import de.flammenfuchs.injections.manager.InjectionsBuilder;
import de.flammenfuchs.injections.manager.InjectionsManager;
import de.flammenfuchs.injections.sample.TestA;
import de.flammenfuchs.injections.sample.TestB;
import de.flammenfuchs.injections.sample.TestC;
import de.flammenfuchs.injections.sample.TestD;
import de.flammenfuchs.javalib.logging.LogLevel;
import lombok.SneakyThrows;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnnotationProcessorTest {

    static InjectionsManager manager;

    @BeforeAll
    void initTest() {
        manager = InjectionsBuilder.create().setLoggerWithLogLevel(LogLevel.NONE).addTarget(this).build();
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

    @Test
    @SneakyThrows
    public void testObjectTimer() {
        TestC test = new TestC();
        manager.processObject(test);
        assertEquals(0, test.a);
        assertEquals(1, test.b);
        Awaitility.await().atMost(110, TimeUnit.MILLISECONDS)
                .until(() -> test.a == 1 && test.b == 2);

        assertEquals(1, test.a);
        assertEquals(2, test.b);

        Awaitility.await().atMost(200, TimeUnit.MILLISECONDS)
                .until(() -> test.b >= 3);

        assertTrue(test.b >= 3);
    }
}