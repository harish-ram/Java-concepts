package org.slf4j;

/**
 * Minimal compile-time stub for SLF4J Logger to satisfy IDE/javac builds.
 * At runtime the real SLF4J implementation from Maven dependencies will be used.
 */
public interface Logger {
    void debug(String msg, Object... args);
    void info(String msg, Object... args);
    void error(String msg, Object... args);
    void error(String msg, Throwable t);
}
