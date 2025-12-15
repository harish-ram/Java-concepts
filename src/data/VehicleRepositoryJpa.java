package data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import models.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.DbMigrations;

/**
 * JPA-backed implementation of VehicleRepository
 */
public class VehicleRepositoryJpa implements VehicleRepository {
    private final String url;
    private final String user;
    private final String pass;
    private EntityManagerFactory emf;
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleRepositoryJpa.class);

    public VehicleRepositoryJpa(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    @Override
    public void init() throws Exception {
        // Run Flyway migrations first
        DbMigrations.migrate(url, user, pass);

        Map<String, Object> props = new HashMap<>();
        props.put("javax.persistence.jdbc.url", url);
        props.put("javax.persistence.jdbc.user", user);
        props.put("javax.persistence.jdbc.password", pass);
        // Ensure Hibernate uses correct dialect implicitly from driver
        try {
            emf = Persistence.createEntityManagerFactory("vehiclesPU", props);
            if (emf != null) {
                LOGGER.debug("Created EMF: {} (open={})", emf.getClass().getName(), emf.isOpen());
            }
        } catch (Throwable t) {
            LOGGER.error("Error creating EntityManagerFactory: {}", t.getMessage(), t);
            throw new IllegalStateException("Failed to create EntityManagerFactory: " + t.getMessage(), t);
        }
        // If the provider isn't available (stubbed Persistence returns a non-open EMF), detect and fail
        if (emf == null || !emf.isOpen()) {
            emf = null;
            throw new IllegalStateException("No JPA provider found on classpath; ensure Hibernate/JPA provider is available (run with Maven or include provider jars)");
        }
    }

    private EntityManager em() {
        return emf.createEntityManager();
    }

    @Override
    public void addVehicle(Vehicle v) throws Exception {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.persist(v);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean removeVehicleById(String id) throws Exception {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            Vehicle v = em.find(Vehicle.class, id);
            if (v == null) { em.getTransaction().commit(); return false; }
            em.remove(v);
            em.getTransaction().commit();
            return true;
        } finally {
            em.close();
        }
    }

    @Override
    public boolean updateVehicle(Vehicle v) throws Exception {
        EntityManager em = em();
        try {
            em.getTransaction().begin();
            em.merge(v);
            em.getTransaction().commit();
            return true;
        } finally {
            em.close();
        }
    }

    @Override
    public List<Vehicle> getAllVehicles() throws Exception {
        EntityManager em = em();
        try {
            TypedQuery<Vehicle> q = em.createQuery("SELECT v FROM Vehicle v", Vehicle.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Vehicle getVehicleById(String id) throws Exception {
        EntityManager em = em();
        try {
            return em.find(Vehicle.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * Close the EntityManagerFactory when shutting down
     */
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
