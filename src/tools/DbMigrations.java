package tools;

/**
 * Helper to run Flyway migrations programmatically when Flyway is available.
 * Uses reflection so compilation does not require Flyway on the classpath.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbMigrations {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbMigrations.class);

    public static void migrate(String url, String user, String pass) {
        try {
            // Use reflection to call Flyway.configure().dataSource(...).locations(...).load().migrate();
            Class<?> flywayClass = Class.forName("org.flywaydb.core.Flyway");
            Object configure = flywayClass.getMethod("configure").invoke(null);
            Object withDataSource = configure.getClass().getMethod("dataSource", String.class, String.class, String.class)
                    .invoke(configure, url, user, pass);
            Object withLocations = null;
            try {
                // Flyway 6+ uses varargs String... for locations (which at runtime is String[])
                withLocations = withDataSource.getClass().getMethod("locations", String[].class)
                        .invoke(withDataSource, (Object) new String[] { "classpath:db/migration" });
            } catch (NoSuchMethodException nsme) {
                // Older Flyway used a single String parameter - try that
                withLocations = withDataSource.getClass().getMethod("locations", String.class)
                        .invoke(withDataSource, "classpath:db/migration");
            }
            Object flyway = withLocations.getClass().getMethod("load").invoke(withLocations);
            flyway.getClass().getMethod("migrate").invoke(flyway);
        } catch (ClassNotFoundException cnf) {
            LOGGER.info("Flyway not available on classpath; skipping migrations");
        } catch (Throwable e) {
            LOGGER.error("Error running migrations: {}", e.getMessage(), e);
        }
    }
}
