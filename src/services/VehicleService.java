package services;

import data.VehicleRepository;
import java.util.*;
import models.Vehicle;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service layer to abstract business logic from controllers and UI.
 */
@Service
public class VehicleService {
    private final VehicleRepository repo;

    @Autowired
    public VehicleService(VehicleRepository repo) {
        this.repo = repo;
        try { this.repo.init(); } catch (Exception ignored) {}
    }

    public void addVehicle(Vehicle v) throws Exception { repo.addVehicle(v); }
    public boolean removeVehicleById(String id) throws Exception { return repo.removeVehicleById(id); }
    public boolean updateVehicle(Vehicle v) throws Exception { return repo.updateVehicle(v); }
    public List<Vehicle> getAllVehicles() throws Exception { return repo.getAllVehicles(); }
    public Vehicle getVehicleById(String id) throws Exception { return repo.getVehicleById(id); }

    // search with case-insensitive substring for brand and exact type match
    public List<Vehicle> filterVehicles(String brandFilter, String typeFilter) throws Exception {
        String b = brandFilter == null ? "" : brandFilter.trim().toLowerCase();
        String t = typeFilter == null ? "" : typeFilter.trim();
        List<Vehicle> list = getAllVehicles();
        List<Vehicle> out = new ArrayList<>();
        for (Vehicle v : list) {
            boolean okBrand = b.isEmpty() || (v.getBrand() != null && v.getBrand().toLowerCase().contains(b));
            boolean okType = t.isEmpty() || v.getClass().getSimpleName().equalsIgnoreCase(t);
            if (okBrand && okType) out.add(v);
        }
        return out;
    }

    // convenience save/load if repository supports in-memory JSON
    public void saveToJson(String filename) throws Exception {
        try { if (repo instanceof data.VehicleDatabaseRepository) { ((data.VehicleDatabaseRepository) repo).saveToJson(filename); } }
        catch (Exception e) { throw e; }
    }
    public void loadFromJson(String filename) throws Exception {
        try { if (repo instanceof data.VehicleDatabaseRepository) { ((data.VehicleDatabaseRepository) repo).loadFromJson(filename); } }
        catch (Exception e) { throw e; }
    }
}
