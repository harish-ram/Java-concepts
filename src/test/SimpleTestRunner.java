package test;

import data.VehicleDatabase;
import models.Bike;
import models.Car;
import models.Motorcycle;
import models.Truck;

public class SimpleTestRunner {
    public static void main(String[] args) {
        VehicleDatabase db = new VehicleDatabase();
        db.addVehicle(new Car("Toyota", "Camry", 2023, 4, "Hybrid"));
        db.addVehicle(new Bike("Harley-Davidson", "Street 750", 2022, false, "Cruiser"));
        db.addVehicle(new Truck("Ford", "F-150", 2021, 1500.0, false));
        db.addVehicle(new Motorcycle("Yamaha", "R1", 2022, 1000, "Sports"));

        if (db.getTotalCount() != 4) {
            System.err.println("FAILED: Expected 4 vehicles, got " + db.getTotalCount());
            System.exit(1);
        }

        double avg = db.getAverageMaxSpeed();
        if (avg <= 0) {
            System.err.println("FAILED: Expected average max speed > 0");
            System.exit(1);
        }

        db.saveToJson("test_vehicles.json");
        VehicleDatabase db2 = new VehicleDatabase();
        db2.loadFromJson("test_vehicles.json");
        if (db2.getTotalCount() != 4) {
            System.err.println("FAILED: Load from JSON resumed incorrect count: " + db2.getTotalCount());
            System.exit(1);
        }
        // Test remove by id
        String idToRemove = db2.getAllVehicles().get(0).getId();
        boolean removed = db2.removeVehicleById(idToRemove);
        if (!removed || db2.getTotalCount() != 3) {
            System.err.println("FAILED: removeVehicleById did not work correctly");
            System.exit(1);
        }

        // Test replace vehicle by id (car -> truck)
        db2.addVehicle(new Car("ReplaceTest", "C2", 2020, 2, "Petrol"));
        String idToReplace = db2.getAllVehicles().stream().filter(v -> v.getBrand().equals("ReplaceTest")).findFirst().get().getId();
        boolean replaced = db2.replaceVehicleById(idToReplace, new Truck("Replaced", "T2", 2021, 1000.0, true));
        if (!replaced) {
            System.err.println("FAILED: replaceVehicleById did not report success");
            System.exit(1);
        }
        System.out.println("All simple tests passed.");
    }
}
