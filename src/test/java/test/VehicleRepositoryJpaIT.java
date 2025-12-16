package test;

import org.junit.Test;
import static org.junit.Assert.*;

import data.VehicleRepositoryJpa;
import models.Car;
import models.Vehicle;

import java.util.List;

public class VehicleRepositoryJpaIT {
    @Test
    public void jpaCrudAndMigrations() throws Exception {
        String url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String pass = "";
        VehicleRepositoryJpa repo = new VehicleRepositoryJpa(url, user, pass);

        // init should run Flyway migrations and throw if JPA provider missing
        repo.init();

        // add vehicles
        Vehicle c1 = new Car("Toyota", "Corolla", 2020, 4, "Petrol");
        Vehicle c2 = new Car("Honda", "Civic", 2019, 4, "Petrol");
        repo.addVehicle(c1);
        repo.addVehicle(c2);

        List<Vehicle> all = repo.getAllVehicles();
        assertTrue("Expected at least 2 vehicles", all.size() >= 2);

        Vehicle fetched = repo.getVehicleById(c1.getId());
        assertNotNull(fetched);

        // cleanup
        repo.removeVehicleById(c1.getId());
        repo.removeVehicleById(c2.getId());

        repo.close();
    }
}
