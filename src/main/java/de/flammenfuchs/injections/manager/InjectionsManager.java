package de.flammenfuchs.injections.manager;

import com.google.common.reflect.ClassPath;
import de.flammenfuchs.injections.annotationProcessor.ProcessorAdapter;
import de.flammenfuchs.injections.bootstrap.InjectionsBootstrap;
import de.flammenfuchs.injections.logging.LogLevel;
import de.flammenfuchs.injections.logging.Logger;
import de.flammenfuchs.injections.registry.AnnotationRegistry;
import de.flammenfuchs.injections.registry.TypeConsumerRegistry;
import de.flammenfuchs.javalib.reflect.ClassScanner;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.List;

public class InjectionsManager {

    private final InjectionsBootstrap bootstrap;

    @Getter
    private Logger logger;

    @Getter
    private final AnnotationRegistry annotationRegistry = new AnnotationRegistry();
    @Getter
    private final TypeConsumerRegistry typeConsumerRegistry = new TypeConsumerRegistry();
    @Getter
    private final ProcessorAdapter adapter;

    public InjectionsManager(InjectionsBootstrap bootstrap) {
        this.bootstrap = bootstrap;

        this.logger = new Logger(bootstrap.logLevel(), bootstrap.logFormat());
        this.adapter = new ProcessorAdapter(bootstrap, annotationRegistry, typeConsumerRegistry, logger);
    }

    @SneakyThrows
    public void start() {
        this.logger.info(LogLevel.BASIC, "Start processing... (LogLevel: " + bootstrap.logLevel().name() + ")");
        this.logger.info(LogLevel.EXTENDED, "Current configuration:");
        this.logger.info(LogLevel.EXTENDED, "  -> package=" + bootstrap.mainClass());
        this.logger.info(LogLevel.EXTENDED, "  -> allowExternalInjectable=" + bootstrap.allowExternalInjectable());
        this.logger.info(LogLevel.EXTENDED, "  -> addDefault=" + bootstrap.addDefault());
        this.logger.info(LogLevel.EXTENDED, "  -> processAllClasses=" + bootstrap.processAllClasses());
        this.logger.info(LogLevel.EXTENDED, "  -> logFormatString=" + bootstrap.logFormat());

        this.logger.info(LogLevel.BASIC, "Scanning...");
        long scan = System.currentTimeMillis();
        ClassScanner scanner = new ClassScanner(bootstrap.mainClass());
        if (bootstrap.classLoader().isPresent()) {
            scanner.setClassLoader(bootstrap.classLoader().get());
        }
        scanner.addIgnoredPackages(bootstrap.ignoredPackages());

        if (bootstrap.addDefault()) {
            this.adapter.registerDefaults();
            this.logger.info(LogLevel.BASIC, "Register default processors.");
        }
        int[] found = adapter.scan(scanner.scan());
        this.logger.info(LogLevel.BASIC, "Found " + found[0] + " classes");
        this.logger.info(LogLevel.EXTENDED, "Found " + found[1] + " fields");
        this.logger.info(LogLevel.EXTENDED, "Found " + found[2] + " methods");
        this.logger.info(LogLevel.BASIC, "Scanning completed. Took " + (System.currentTimeMillis() - scan) + "ms");

        this.logger.info(LogLevel.BASIC, "Process...");
        long process = System.currentTimeMillis();
        this.adapter.process();
        this.logger.info(LogLevel.BASIC, "Processing completed. Took " + (System.currentTimeMillis() - process) + "ms");
    }


}
