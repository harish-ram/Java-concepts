package data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import models.Vehicle;

@Repository
public interface VehicleJpaRepository extends JpaRepository<Vehicle, String> {
}
