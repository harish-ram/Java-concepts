package org.slf4j;

/**
 * Minimal compile-time stub for SLF4J LoggerFactory to satisfy IDE/javac builds.
 * Returns a simple logger that writes to stdout/stderr. At runtime the real
 * SLF4J implementation from Maven dependencies will be used instead.
 */
public class LoggerFactory {
    public static Logger getLogger(Class<?> cls) {
        return new SimpleLogger(cls == null ? "root" : cls.getName());
    }

    private static class SimpleLogger implements Logger {
        private final String name;
        SimpleLogger(String name) { this.name = name; }
        private String prefix() { return "[" + name + "] "; }
        public void debug(String msg, Object... args) { System.out.println(prefix() + String.format(msg.replace("{}","%s"), args)); }
        public void info(String msg, Object... args) { System.out.println(prefix() + String.format(msg.replace("{}","%s"), args)); }
        public void error(String msg, Object... args) { System.err.println(prefix() + String.format(msg.replace("{}","%s"), args)); }
        public void error(String msg, Throwable t) { System.err.println(prefix() + msg); t.printStackTrace(System.err); }
    }
}
