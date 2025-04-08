package de.flammenfuchs.injections.sample;

import de.flammenfuchs.injections.annon.Inject;
import de.flammenfuchs.injections.annon.Scoped;

@Scoped
public class TestA {

    @Inject
    public TestB a;
}
