package javax.persistence;

public interface EntityManagerFactory {
    EntityManager createEntityManager();
    void close();
    boolean isOpen();
}
