package de.flammenfuchs.injections.manager;

import de.flammenfuchs.javalib.lang.triple.Triple;
import de.flammenfuchs.javalib.logging.LogLevel;
import de.flammenfuchs.javalib.logging.Logger;
import de.flammenfuchs.javalib.reflect.scanner.ClassScanner;
import de.flammenfuchs.javalib.reflect.scanner.DefaultClassScanner;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to build a {@link InjectionsManager}
 */
public class InjectionsBuilder {
    public static final String DEFAULT_LOGGER_FORMAT = "[%1$tT] [%2$s/%3$s] %4$s %n";
    public static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.BASIC;
    public static final String DEFAULT_LOGGER_NAME = "injections";

    /**
     * Create a new {@link InjectionsBuilder} to build an {@link InjectionsManager}<br>
     * You can use {@link InjectionsBuilder InjectionsBuilder.create().build();}
     * to create a {@link InjectionsManager} with default values
     *
     * @return A new builder instance
     */
    public static InjectionsBuilder create() {
        return new InjectionsBuilder();
    }

    protected InjectionsBuilder() {
    }

    private final List<Triple<ClassLoader, String, String[]>> targets = new ArrayList<>();

    private boolean defaultAnnotations = true;
    private Logger logger = new Logger(DEFAULT_LOGGER_NAME, DEFAULT_LOG_LEVEL,
            DEFAULT_LOGGER_FORMAT, true);

    /**
     * A supplier to create a {@link ClassScanner} for every targeted {@link ClassLoader}
     */
    private ClassScannerSupplier supplier = (loader, topPackage, ignoredPackages) -> {
        ClassScanner scanner = new DefaultClassScanner(topPackage, loader);
        scanner.addIgnoredPackages(ignoredPackages);
        return scanner;
    };

    /**
     * Add a target to scan later
     *
     * @param classLoader The {@link ClassLoader} where the target is located
     * @param topPackage The top packageName ("" - when you want to scan in all classes)
     * @param ignoredPackages All ignored packages while scanning. Empty if nothing to ignore
     * @return current builder instance
     */
    public InjectionsBuilder addTarget(ClassLoader classLoader, String topPackage, String... ignoredPackages) {
        this.targets.add(Triple.finalTriple(classLoader, topPackage, ignoredPackages));
        return this;
    }

    /**
     * Add a target to scan later
     *
     * @param classLoader The {@link ClassLoader} where the target is located
     * @param objectInTopPackage An object which is located in the top package
     * @param ignoredPackages All ignored packages while scanning. Empty if nothing to ignore
     * @return current builder instance
     */
    public InjectionsBuilder addTarget(ClassLoader classLoader, Object objectInTopPackage, String... ignoredPackages) {
        return addTarget(classLoader, objectInTopPackage.getClass().getPackageName(), ignoredPackages);
    }

    /**
     * Add a target to scan later
     *
     * @param classLoader The {@link ClassLoader} where the target is located
     * @param topPackage The top {@link Package}
     * @param ignoredPackages All ignored packages while scanning. Empty if nothing to ignore
     * @return current builder instance
     */
    public InjectionsBuilder addTarget(ClassLoader classLoader, Package topPackage, String... ignoredPackages) {
        return addTarget(classLoader, topPackage.getName(), ignoredPackages);
    }

    /**
     * Add a target to scan later
     *
     * @param topObject An object which is located in the top package
     * @param ignoredPackages All ignored packages while scanning. Empty if nothing to ignore
     * @return current builder instance
     */
    public InjectionsBuilder addTarget(Object topObject, String... ignoredPackages) {
        return addTarget(topObject.getClass().getClassLoader(), topObject.getClass().getPackageName(), ignoredPackages);
    }

    /**
     * Disable the default Annotations e.g {@link de.flammenfuchs.injections.annon.Instantiate}
     *
     * @return current builder instance
     */
    public InjectionsBuilder disableDefaultAnnotations() {
        this.defaultAnnotations = false;
        return this;
    }

    /**
     * Enable the default Annotations e.g {@link de.flammenfuchs.injections.annon.Instantiate}
     *
     * @return current builder instance
     */
    public InjectionsBuilder enableDefaultAnnotations() {
        this.defaultAnnotations = true;
        return this;
    }

    /**
     * Set the logger
     *
     * @param logger The {@link Logger} instance
     * @return current builder instance
     */
    public InjectionsBuilder setLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    /**
     * Set a new instance of a {@link Logger} with a format string
     *
     * @param formatString the formatting string
     * @return current builder instance
     */
    public InjectionsBuilder setLoggerWithFormatString(String formatString) {
        this.logger = new Logger(DEFAULT_LOGGER_NAME, DEFAULT_LOG_LEVEL, formatString, true);
        return this;
    }

    /**
     * Set a new instance of a {@link Logger} with a log level
     *
     * @param logLevel the log level
     * @return current builder instance
     */
    public InjectionsBuilder setLoggerWithLogLevel(LogLevel logLevel) {
        this.logger = new Logger(DEFAULT_LOGGER_NAME, logLevel, DEFAULT_LOGGER_FORMAT, true);
        return this;
    }

    /**
     * Set a new instance of a {@link Logger} with the given parameters
     *
     * @param name the logger name e.g injections
     * @param logLevel the log level
     * @param formatString the formatting string
     * @param highlightNonInfo should errors and warns be highlighted
     * @return current builder instance
     */
    public InjectionsBuilder setLogger(String name, LogLevel logLevel, String formatString, boolean highlightNonInfo) {
        this.logger = new Logger(name, logLevel, formatString, highlightNonInfo);
        return this;
    }

    /**
     * Set a new instance of a {@link Logger} with the given parameters
     *
     * @param name the logger name e.g injections
     * @param logLevel the log level
     * @param formatString the formatting string
     * @return current builder instance
     */
    public InjectionsBuilder setLogger(String name, LogLevel logLevel, String formatString) {
        this.logger = new Logger(name, logLevel, formatString, true);
        return this;
    }

    /**
     * A supplier to create a {@link ClassScanner} for each classloader
     *
     * @param supplier the supplier instance
     * @return current builder instance
     */
    public InjectionsBuilder setClassScannerSupplier(ClassScannerSupplier supplier) {
        this.supplier = supplier;
        return this;
    }

    /**
     * Build the actual manager
     *
     * @return the actual manager
     */
    public InjectionsManager build() {
        return new InjectionsManager(targets, defaultAnnotations, logger, supplier);
    }

    /**
     * A supplier to create a {@link ClassScanner} for every targeted {@link ClassLoader}
     */
    public interface ClassScannerSupplier {

        /**
         * This method creates the {@link ClassScanner} for a given target
         *
         * @param loader The {@link ClassLoader} where the target is located
         * @param topPackage The top {@link Package}
         * @param ignoredPackages All ignored packages while scanning. Empty if nothing to ignore
         * @return the new {@link ClassScanner}
         */
        ClassScanner supply(ClassLoader loader, String topPackage, String[] ignoredPackages);
    }
}
