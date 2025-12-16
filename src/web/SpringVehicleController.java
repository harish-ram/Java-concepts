package web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import data.VehicleDatabaseRepository;
import models.Vehicle;

@RestController
@RequestMapping("/api/vehicles")
public class SpringVehicleController {

    private final data.VehicleRepository repo;

    public SpringVehicleController(data.VehicleRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Vehicle> listAll() throws Exception {
        return repo.getAllVehicles();
    }

    @GetMapping("/{id}")
    public Vehicle getById(@PathVariable("id") String id) throws Exception {
        return repo.getVehicleById(id);
    }

    @PostMapping("/loadJson")
    public boolean loadJson() {
        try {
            // If backing repository supports loadFromJson, call it. Use reflection to avoid tight coupling.
            try {
                java.lang.reflect.Method m = repo.getClass().getMethod("loadFromJson", String.class);
                m.invoke(repo, "vehicles.json");
            } catch (NoSuchMethodException nsme) {
                // not supported; ignore
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
