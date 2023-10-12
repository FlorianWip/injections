package de.flammenfuchs.injections.sample;

import de.flammenfuchs.injections.annon.Instantiate;
import de.flammenfuchs.injections.annon.Invoke;

@Instantiate
public class TestB {

    public int a;

    @Invoke
    public void a() {
        a = 7;
    }
}
