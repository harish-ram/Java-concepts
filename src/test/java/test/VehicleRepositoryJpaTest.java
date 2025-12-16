package test;

import org.junit.*;
import static org.junit.Assert.*;
import data.VehicleRepositoryJpa;
import models.Car;

public class VehicleRepositoryJpaTest {
    @Test
    public void basicAddFetch() throws Exception {
        String url = "jdbc:h2:mem:testdb2;DB_CLOSE_DELAY=-1";
        VehicleRepositoryJpa repo = new VehicleRepositoryJpa(url, "sa", "");
        try {
            repo.init();
            Car c = new Car("Mazda", "3", 2020, 4, "Petrol");
            repo.addVehicle(c);
            assertNotNull(repo.getVehicleById(c.getId()));
        } finally {
            repo.close();
        }
    }
}
