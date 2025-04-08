package de.flammenfuchs.injections.sample;

import de.flammenfuchs.injections.annon.Inject;
import de.flammenfuchs.injections.annon.Startup;

public class TestD {

    @Inject
    public TestA a;

    public int b;

    @Startup
    private void b() {
        b = 7;
    }
}
