package de.flammenfuchs.injections.sample;

import de.flammenfuchs.injections.annon.Timer;

public class TestC {

    public int a = 0;
    public int b = 0;

    @Timer(delay = 70)
    public void a() {
        a = 1;
    }

    @Timer(delay = 0, period = 70)
    public void b() {
        b++;
    }


}
