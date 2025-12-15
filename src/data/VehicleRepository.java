package data;

import java.util.List;
import models.Vehicle;

public interface VehicleRepository {
    void init() throws Exception; // optional init (schema etc.)
    void addVehicle(Vehicle v) throws Exception;
    boolean removeVehicleById(String id) throws Exception;
    boolean updateVehicle(Vehicle v) throws Exception;
    List<Vehicle> getAllVehicles() throws Exception;
    Vehicle getVehicleById(String id) throws Exception;
}
