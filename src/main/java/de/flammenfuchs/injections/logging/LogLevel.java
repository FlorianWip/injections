package de.flammenfuchs.injections.logging;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum LogLevel {
    /**
     * Disable logging
     */
    NONE(0),
    /**
     * Basic logging. Show current state and elapsed time
     */
    BASIC(1),
    /**
     * Log all
     */
    EXTENDED(2);

    private final int level;
}
