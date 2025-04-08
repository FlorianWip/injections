package de.flammenfuchs.injections.sample;

import de.flammenfuchs.injections.annon.Scoped;
import de.flammenfuchs.injections.annon.Startup;

@Scoped
public class TestB {

    public int a;

    @Startup
    public void a() {
        a = 7;
    }
}
