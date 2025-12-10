import org.junit.*;
import static org.junit.Assert.*;
import data.VehicleDatabase;
import models.Car;
import models.Bike;
import models.Truck;
import models.Motorcycle;

public class VehicleDatabaseJUnitTest {
    @Test
    public void testAddAndCount() {
        VehicleDatabase db = new VehicleDatabase();
        db.addVehicle(new Car("Toyota", "Camry", 2023, 4, "Hybrid"));
        db.addVehicle(new Bike("Harley-Davidson", "Street 750", 2022, false, "Cruiser"));
        db.addVehicle(new Truck("Ford", "F-150", 2021, 1500.0, false));
        db.addVehicle(new Motorcycle("Yamaha", "R1", 2022, 1000, "Sports"));
        assertEquals(4, db.getTotalCount());
        db.countByType(); // prints counts (sanity check)
    }

    @Test
    public void testSaveLoadJson() {
        VehicleDatabase db = new VehicleDatabase();
        db.addVehicle(new Car("Test", "C1", 2020, 2, "Petrol"));
        db.addVehicle(new Truck("Test", "T1", 2019, 500.0, true));
        db.saveToJson("test_junit.json");

        VehicleDatabase db2 = new VehicleDatabase();
        db2.loadFromJson("test_junit.json");
        assertEquals(2, db2.getTotalCount());
    }
}
