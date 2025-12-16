package test;

import org.junit.*;
import static org.junit.Assert.*;
import data.VehicleDatabase;

public class VehicleDatabaseBrandFilterTest {
    @Test
    public void brandFilterWorks() {
        VehicleDatabase db = new VehicleDatabase();
        db.addVehicle(new models.Car("Toyota", "Camry", 2023, 4, "Hybrid"));
        db.addVehicle(new models.Car("Toyota", "Corolla", 2021, 4, "Petrol"));
        assertEquals(2, db.filterByBrand("Toyota").size());
    }
}
