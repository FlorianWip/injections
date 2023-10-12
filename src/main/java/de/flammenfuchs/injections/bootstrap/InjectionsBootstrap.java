package de.flammenfuchs.injections.bootstrap;

import de.flammenfuchs.javalib.logging.LogLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Optional;

@RequiredArgsConstructor
@Setter
@Accessors(fluent = true)
@Getter
public class InjectionsBootstrap {

    public static InjectionsBootstrap create(String mainClass) {
        return new InjectionsBootstrap(mainClass);
    }

    public static InjectionsBootstrap create(Object main) {
        return create(main.getClass().getPackage().getName());
    }

    private final String mainClass;

    /**
     * Is it allowed to add external objects to be injectable
     */
    private boolean allowExternalInjectable = true;
    /**
     * Add default annotations
     */
    private boolean addDefault = true;
    /**
     * Should we process all Classes or only annotated with @Instantiate
     */
    private boolean processAllClasses = false;
    /**
     * Ignore Packages
     */
    private String[] ignoredPackages = new String[] {};
    /**
     * Define a custom classloader to override the default
     */
    private Optional<ClassLoader> classLoader = Optional.empty();
    /**
     * FormatString for Logger
     */
    private String logFormat = "[%1$tT] [%2$s/%3$s] %4$s %n";
    /**
     * How much should be logged
     */
    private LogLevel logLevel = LogLevel.BASIC;
}
