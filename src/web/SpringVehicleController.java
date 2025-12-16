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

    private final VehicleDatabaseRepository repo = new VehicleDatabaseRepository();

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
            repo.loadFromJson("vehicles.json");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
