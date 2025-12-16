package data;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import models.Vehicle;

@Repository
public class SpringDataVehicleRepository implements VehicleRepository {

    private final VehicleJpaRepository jpaRepo;

    @Autowired
    public SpringDataVehicleRepository(VehicleJpaRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public void init() throws Exception {
        // Flyway will run automatically in Spring Boot; no-op here
    }

    @Override
    public void addVehicle(Vehicle v) throws Exception {
        jpaRepo.save(v);
    }

    @Override
    public boolean removeVehicleById(String id) throws Exception {
        if (!jpaRepo.existsById(id)) return false;
        jpaRepo.deleteById(id);
        return true;
    }

    @Override
    public boolean updateVehicle(Vehicle v) throws Exception {
        if (!jpaRepo.existsById(v.getId())) return false;
        jpaRepo.save(v);
        return true;
    }

    @Override
    public List<Vehicle> getAllVehicles() throws Exception {
        return jpaRepo.findAll();
    }

    @Override
    public Vehicle getVehicleById(String id) throws Exception {
        Optional<Vehicle> opt = jpaRepo.findById(id);
        return opt.orElse(null);
    }

}
