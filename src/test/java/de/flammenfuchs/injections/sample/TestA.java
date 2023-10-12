package de.flammenfuchs.injections.sample;

import de.flammenfuchs.injections.annon.Inject;
import de.flammenfuchs.injections.annon.Instantiate;

@Instantiate
public class TestA {

    @Inject
    public TestB a;
}
