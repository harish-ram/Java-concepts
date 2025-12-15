import org.junit.*;
import static org.junit.Assert.*;
import data.VehicleDatabase;
import models.Car;
import models.Bike;

public class VehicleDatabaseBrandFilterTest {
    @Test
    public void testGetVehiclesByBrand_partialMatchCasesInsensitive() {
        VehicleDatabase db = new VehicleDatabase();
        db.addVehicle(new Car("Harley-Davidson", "Street 750", 2022, 2, "Petrol"));
        db.addVehicle(new Bike("Honda", "CBR", 2020, false, "Sports"));
        db.addVehicle(new Car("Harley Davidson", "Softail", 2021, 2, "Petrol"));

        // Expect partial and case-insensitive matches: "harley" should match both harley entries
        assertEquals(2, db.getVehiclesByBrand("harley").size());
        // "hon" should match Honda
        assertEquals(1, db.getVehiclesByBrand("hon").size());
    }
}
