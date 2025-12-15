package test;

import data.VehicleDaoJdbc;
import models.*;
import java.sql.SQLException;
import java.util.List;

public class JdbcDaoTestRunner {
    public static void main(String[] args) {
        String url = "jdbc:h2:mem:vehicledb;DB_CLOSE_DELAY=-1";
        try {
            // Create DAO and ensure schema is ready
            VehicleDaoJdbc dao = new VehicleDaoJdbc(url);
            dao.init();
            // Insert a couple of vehicles
            dao.addVehicle(new Car("Toyota", "Camry", 2021, 4, "Hybrid"));
            dao.addVehicle(new Bike("Honda", "CBR", 2020, false, "Sports"));
            List<Vehicle> list = dao.getAllVehicles();
            System.out.println("Inserted vehicles: " + list.size());
            if (list.size() < 2) { System.err.println("FAILED: expected >= 2 vehicles"); System.exit(1);}        
            // Update a vehicle
            Vehicle v = list.get(0);
            System.out.println("Updating first vehicle id=" + v.getId());
            // Attempt to update year via creating a new instance preserving id
            if (v instanceof Car) {
                Car c = (Car)v;
                Car updated = new Car(c.getId(), c.getBrand(), c.getModel(), c.getYear() + 1, c.getNumDoors(), c.getFuelType());
                boolean ok = dao.updateVehicle(updated);
                if (!ok) { System.err.println("FAILED: update did not report success"); System.exit(1); }
            }
            // Delete the second vehicle
            Vehicle todelete = list.size() > 1 ? list.get(1) : null;
            if (todelete != null) {
                boolean d = dao.removeVehicleById(todelete.getId());
                if (!d) { System.err.println("FAILED: delete returned false"); System.exit(1); }
            }
            System.out.println("JDBC DAO tests passed.");
        } catch (NoClassDefFoundError ex) {
            System.err.println("H2 Driver not found -- please add the H2 dependency to the classpath or pom.xml.");
            System.err.println("Maven dependency:");
            System.err.println("<dependency>\n  <groupId>com.h2database</groupId>\n  <artifactId>h2</artifactId>\n  <version>2.1.214</version>\n</dependency>");
            System.exit(2);
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
            System.exit(3);
        }
    }
}
