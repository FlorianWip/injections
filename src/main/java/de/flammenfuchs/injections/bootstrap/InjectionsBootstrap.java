package de.flammenfuchs.injections.bootstrap;

import de.flammenfuchs.javalib.logging.LogLevel;
import de.flammenfuchs.javalib.reflect.scanner.ClassScanner;
import de.flammenfuchs.javalib.reflect.scanner.DefaultClassScanner;
import lombok.AccessLevel;
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
    @Getter(AccessLevel.NONE)
    private ClassLoader classLoader = null;
    /**
     * FormatString for Logger
     */
    private String logFormat = "[%1$tT] [%2$s/%3$s] %4$s %n";
    /**
     * How much should be logged
     */
    private LogLevel logLevel = LogLevel.BASIC;
    /**
     * Define a supplier for a custom ClassScanner
     */
    private ClassScannerSupplier scannerSupplier = DefaultClassScanner::new;
    /**
     * Define a custom classloader to override the default
     */
    public Optional<ClassLoader> classLoader() {
        return Optional.ofNullable(classLoader);
    }

    public interface ClassScannerSupplier {
        ClassScanner supply(String main, ClassLoader loader);
    }
}
