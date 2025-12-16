package javax.persistence;

import java.util.Map;

/**
 * Minimal stub of javax.persistence.Persistence to allow compilation.
 * The implementation here does not provide a real JPA provider; it's
 * only used to satisfy compile-time references. When running with
 * a real JPA provider on the classpath (Maven), that provider will be used instead.
 */
public class Persistence {
    public static EntityManagerFactory createEntityManagerFactory(String name, Map<String, Object> props) {
        // Return a lightweight no-op factory that throws if used at runtime
        return new EntityManagerFactory() {
            @Override
            public EntityManager createEntityManager() {
                throw new UnsupportedOperationException("No JPA provider available at runtime. Use Maven to include Hibernate.");
            }

            @Override
            public void close() {}

            @Override
            public boolean isOpen() { return false; }
        };
    }
}
