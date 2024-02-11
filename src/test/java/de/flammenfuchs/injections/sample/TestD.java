package de.flammenfuchs.injections.sample;

import de.flammenfuchs.injections.annon.Inject;
import de.flammenfuchs.injections.annon.Invoke;

public class TestD {

    @Inject
    public TestA a;

    public int b;

    @Invoke
    private void b() {
        b = 7;
    }
}
