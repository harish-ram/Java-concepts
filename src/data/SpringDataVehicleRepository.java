package data;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import models.Vehicle;

@Repository
public class SpringDataVehicleRepository implements VehicleRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void init() throws Exception {
        // Flyway runs automatically in Spring Boot; ensure EM is available
    }

    @Override
    @Transactional
    public void addVehicle(Vehicle v) throws Exception {
        em.persist(v);
    }

    @Override
    @Transactional
    public boolean removeVehicleById(String id) throws Exception {
        Vehicle v = em.find(Vehicle.class, id);
        if (v == null) return false;
        em.remove(v);
        return true;
    }

    @Override
    @Transactional
    public boolean updateVehicle(Vehicle v) throws Exception {
        if (em.find(Vehicle.class, v.getId()) == null) return false;
        em.merge(v);
        return true;
    }

    @Override
    public List<Vehicle> getAllVehicles() throws Exception {
        TypedQuery<Vehicle> q = em.createQuery("SELECT v FROM Vehicle v", Vehicle.class);
        return q.getResultList();
    }

    @Override
    public Vehicle getVehicleById(String id) throws Exception {
        return em.find(Vehicle.class, id);
    }

}
