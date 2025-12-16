package javax.persistence;

import java.util.List;

public interface EntityManager {
    EntityTransaction getTransaction();
    void persist(Object entity);
    <T> T merge(T entity);
    void remove(Object entity);
    <T> T find(Class<T> entityClass, Object primaryKey);
    <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass);
    void close();
}
