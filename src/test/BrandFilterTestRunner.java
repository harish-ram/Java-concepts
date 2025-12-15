package test;

import data.VehicleDatabase;
import models.Bike;
import models.Car;

public class BrandFilterTestRunner {
    public static void main(String[] args) {
        VehicleDatabase db = new VehicleDatabase();
        db.addVehicle(new Car("Harley-Davidson", "Street 750", 2022, 2, "Petrol"));
        db.addVehicle(new Bike("Honda", "CBR", 2020, false, "Sports"));
        db.addVehicle(new Car("Harley Davidson", "Softail", 2021, 2, "Petrol"));

        // Should match 2 for "harley" if partial matching is supported
        int harleyCount = db.getVehiclesByBrand("harley").size();
        System.out.println("Harley matches: " + harleyCount);
        if (harleyCount != 2) {
            System.err.println("FAILED: expected 2, got " + harleyCount);
            System.exit(1);
        }

        int honCount = db.getVehiclesByBrand("hon").size();
        System.out.println("Hon matches: " + honCount);
        if (honCount != 1) {
            System.err.println("FAILED: expected 1, got " + honCount);
            System.exit(1);
        }

        System.out.println("Brand filter test passed.");
    }
}
