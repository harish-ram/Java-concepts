package data;

import java.util.List;
import models.Vehicle;

/**
 * Adapter that exposes the in-memory VehicleDatabase via VehicleRepository
 */
public class VehicleDatabaseRepository implements VehicleRepository {
    private final VehicleDatabase db;

    public VehicleDatabaseRepository() {
        this.db = new VehicleDatabase();
    }

    public VehicleDatabaseRepository(VehicleDatabase db) {
        this.db = db;
    }

    @Override
    public void init() {
        // nothing to initialize for in-memory DB
    }

    @Override
    public void addVehicle(Vehicle v) {
        db.addVehicle(v);
    }

    @Override
    public boolean removeVehicleById(String id) {
        return db.removeVehicleById(id);
    }

    @Override
    public boolean updateVehicle(Vehicle v) {
        return db.updateVehicle(v);
    }

    @Override
    public List<Vehicle> getAllVehicles() {
        return db.getAllVehicles();
    }

    @Override
    public Vehicle getVehicleById(String id) {
        return db.getVehicleById(id);
    }

    // Save/load methods used by Server for the in-memory repository only
    public void saveToJson(String filename) {
        db.saveToJson(filename);
    }

    public void loadFromJson(String filename) {
        db.loadFromJson(filename);
    }
}
