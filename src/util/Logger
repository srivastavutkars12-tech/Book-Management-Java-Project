package com.bookmanager.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Minimal file-based logger satisfying the application's logging/monitoring
 * non-functional requirement. Writes timestamped INFO/WARN/ERROR lines to app.log.
 */
public final class Logger {

    private static final String LOG_FILE = "app.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Logger() { } // static utility class — not instantiable

    public enum Level { INFO, WARN, ERROR }

    public static void info(String message) {
        write(Level.INFO, message);
    }

    public static void warn(String message) {
        write(Level.WARN, message);
    }

    public static void error(String message) {
        write(Level.ERROR, message);
    }

    private static void write(Level level, String message) {
        String line = String.format("[%s] %-5s %s", LocalDateTime.now().format(FORMATTER), level, message);
        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(line);
        } catch (IOException e) {
            // Logging should never crash the application; fall back to stderr.
            System.err.println("Failed to write log entry: " + e.getMessage());
        }
        if (level == Level.ERROR) {
            System.err.println(line);
        }
    }
}
