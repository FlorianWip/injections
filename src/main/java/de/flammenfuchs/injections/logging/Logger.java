package de.flammenfuchs.injections.logging;

import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class Logger {

    private final LogLevel level;

    private final java.util.logging.Logger utilLogger = java.util.logging.Logger.getLogger("injections");

    public Logger(LogLevel level, String formatString) {
        this.level = level;
        this.utilLogger.setUseParentHandlers(false);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                return String.format(formatString, new Date(), record.getLevel(), record.getMessage());
            }
        });
        this.utilLogger.addHandler(consoleHandler);
    }

    public void info(LogLevel required, String msg) {
        if (level.level() >= required.level()) {
            utilLogger.info(msg);
        }
    }

    public void warn(LogLevel required, String msg) {
        if (level.level() >= required.level()) {
            utilLogger.warning(msg);
        }
    }

    public void err(String msg) {
        utilLogger.severe(msg);
    }

}
