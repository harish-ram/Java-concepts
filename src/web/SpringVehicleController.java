package web;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import models.Bike;
import models.Car;
import models.Motorcycle;
import models.Truck;
import models.Vehicle;
import services.VehicleService;

@RestController
@RequestMapping("/api/vehicles")
public class SpringVehicleController {

    private final VehicleService service;

    public SpringVehicleController(VehicleService service) {
        this.service = service;
    }

    @GetMapping
    public List<Vehicle> listAll(@RequestParam(value = "brand", required = false) String brand,
                                 @RequestParam(value = "type", required = false) String type) throws Exception {
        if ((brand != null && !brand.isEmpty()) || (type != null && !type.isEmpty())) {
            return service.filterVehicles(brand, type);
        }
        return service.getAllVehicles();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getById(@PathVariable("id") String id) throws Exception {
        Vehicle v = service.getVehicleById(id);
        if (v == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(v);
    }

    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> addVehicle(@RequestBody Map<String, Object> body) {
        try {
            String type = (String) body.getOrDefault("type", "");
            if (type == null || type.trim().isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Missing field: type"));
            String brand = (String) body.get("brand");
            String model = (String) body.get("model");
            Integer year = (body.get("year") instanceof Number) ? ((Number) body.get("year")).intValue() : null;
            if (brand == null || brand.trim().isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Missing field: brand"));
            if (model == null || model.trim().isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Missing field: model"));
            if (year == null || year < 1886) return ResponseEntity.badRequest().body(Map.of("error", "Invalid or missing manufacture year"));

            Vehicle v = null;
            if (type.equalsIgnoreCase("car")) {
                Integer doors = (body.get("doors") instanceof Number) ? ((Number) body.get("doors")).intValue() : 4;
                String fuel = (String) body.getOrDefault("fuel", "Petrol");
                v = new Car(brand, model, year, doors, fuel);
            } else if (type.equalsIgnoreCase("bike")) {
                Boolean sidecar = (body.get("sidecar") instanceof Boolean) ? (Boolean) body.get("sidecar") : false;
                String btype = (String) body.getOrDefault("bikeType", "Street");
                v = new Bike(brand, model, year, sidecar, btype);
            } else if (type.equalsIgnoreCase("truck")) {
                Double payload = (body.get("payload") instanceof Number) ? ((Number) body.get("payload")).doubleValue() : 0.0;
                Boolean trailer = (body.get("trailer") instanceof Boolean) ? (Boolean) body.get("trailer") : false;
                v = new Truck(brand, model, year, payload, trailer);
            } else if (type.equalsIgnoreCase("motorcycle")) {
                Integer cc = (body.get("cc") instanceof Number) ? ((Number) body.get("cc")).intValue() : 0;
                String mcat = (String) body.getOrDefault("category", "Sports");
                v = new Motorcycle(brand, model, year, cc, mcat);
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Unknown vehicle type: " + type));
            }
            service.addVehicle(v);
            // return 201 Created with Location header
            return ResponseEntity.created(java.net.URI.create("/api/vehicles/" + v.getId())).body(Map.of("id", v.getId()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateVehicle(@PathVariable("id") String id, @RequestBody Map<String, Object> body) {
        try {
            Vehicle existing = service.getVehicleById(id);
            if (existing == null) return ResponseEntity.notFound().build();
            // Basic update for common fields
            String brand = (String) body.get("brand");
            String model = (String) body.get("model");
            Integer year = (body.get("year") instanceof Number) ? ((Number) body.get("year")).intValue() : null;
            if (brand != null) {
                // use reflection or setters if available
                java.lang.reflect.Method m = existing.getClass().getMethod("setBrand", String.class);
                m.invoke(existing, brand);
            }
            if (model != null) {
                try { java.lang.reflect.Method m2 = existing.getClass().getMethod("setModel", String.class); m2.invoke(existing, model); } catch (NoSuchMethodException nms) {}
            }
            if (year != null && year >= 1886) {
                try { java.lang.reflect.Method m3 = existing.getClass().getMethod("setManufactureYear", int.class); m3.invoke(existing, year); } catch (NoSuchMethodException nms) {}
            }
            service.updateVehicle(existing);
            return ResponseEntity.ok(Map.of("result","updated"));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteVehicleById(@PathVariable("id") String id) {
        try {
            boolean ok = service.removeVehicleById(id);
            return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping(path = "/loadJson")
    public ResponseEntity<String> loadJson() {
        try {
            service.loadFromJson("vehicles.json");
            return ResponseEntity.ok("loaded");
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(ex.getMessage());
        }
    }
}
