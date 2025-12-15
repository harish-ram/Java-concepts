package data;

import java.util.*;
import java.util.stream.*;
import models.*;
import utilities.FileHandler;

/**
 * Demonstrates working with collections and streams
 * for managing vehicle data
 */
public class VehicleDatabase {
    private final List<Vehicle> vehicles;
    
    public VehicleDatabase() {
        vehicles = new ArrayList<>();
    }
    
    /**
     * Add vehicle to database
     */
    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    /**
     * Remove a vehicle by ID
     */
    public boolean removeVehicleById(String id) {
        return vehicles.removeIf(v -> v.getId().equals(id));
    }

    /**
     * Update vehicle by ID; replace with provided vehicle (assuming same id)
     */
    public boolean updateVehicle(Vehicle newVehicle) {
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getId().equals(newVehicle.getId())) {
                vehicles.set(i, newVehicle);
                return true;
            }
        }
        return false;
    }

    /**
     * Replace vehicle by id: remove the existing and add the new one (ID may change)
     */
    public boolean replaceVehicleById(String id, Vehicle newVehicle) {
        boolean removed = removeVehicleById(id);
        addVehicle(newVehicle);
        return removed;
    }

    /**
     * Update vehicle preserving the id: if the provided vehicle has the same id, replace it.
     */
    public boolean updateVehiclePreserveId(Vehicle updatedVehicle) {
        return updateVehicle(updatedVehicle);
    }
    
    /**
     * Get all vehicles
     */
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles);
    }
    
    /**
     * Display all vehicles
     */
    public void displayAllVehicles() {
        System.out.println("\n=== All Vehicles ===");
        vehicles.forEach(Vehicle::displayInfo);
    }
    
    /**
     * Filter vehicles by brand using streams
     *
     * This method performs a case-insensitive substring match - providing
     * a partial brand string will return matches that contain it (e.g. "hon" -> "Honda").
     * If the provided brand is null or empty, the full list is returned.
     */
    public List<Vehicle> getVehiclesByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) return getAllVehicles();
        final String q = brand.trim().toLowerCase();
        return vehicles.stream()
                .filter(v -> v.getBrand() != null && v.getBrand().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }
    
    /**
     * Get vehicles manufactured in specific year
     */
    public List<Vehicle> getVehiclesByYear(int year) {
        return vehicles.stream()
                .filter(v -> v.getYear() == year)
                .collect(Collectors.toList());
    }

    /**
     * Backwards-compatible alias for brand filtering used by older tests/code.
     */
    public List<Vehicle> filterByBrand(String brand) {
        return getVehiclesByBrand(brand);
    }
    
    /**
     * Get average max speed of all vehicles
     */
    public double getAverageMaxSpeed() {
        return vehicles.stream()
                .mapToDouble(Vehicle::getMaxSpeed)
                .average()
                .orElse(0.0);
    }
    
    /**
     * Group vehicles by brand
     */
    public Map<String, List<Vehicle>> groupByBrand() {
        return vehicles.stream()
                .collect(Collectors.groupingBy(Vehicle::getBrand));
    }
    
    /**
     * Count vehicles by type
     */
    public void countByType() {
        long cars = vehicles.stream().filter(v -> v instanceof Car).count();
        long bikes = vehicles.stream().filter(v -> v instanceof Bike).count();
        long trucks = vehicles.stream().filter(v -> v instanceof Truck).count();
        long motorcycles = vehicles.stream().filter(v -> v instanceof Motorcycle).count();
        System.out.println("\nVehicle Count: Cars=" + cars + ", Bikes=" + bikes + ", Trucks=" + trucks + ", Motorcycles=" + motorcycles);
    }
    
    /**
     * Get fastest vehicle
     */
    public Vehicle getFastestVehicle() {
        return vehicles.stream()
                .max(Comparator.comparingDouble(Vehicle::getMaxSpeed))
                .orElse(null);
    }
    
    /**
     * Get vehicle count
     */
    public int getTotalCount() {
        return vehicles.size();
    }

    /**
     * Clear all vehicles
     */
    public void clear() {
        vehicles.clear();
    }


    public void saveToJson(String filename) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Vehicle v : vehicles) {
            if (!first) sb.append(",\n"); first = false;
            sb.append("{");
            sb.append("\"id\":\"").append(v.getId()).append("\"");
            sb.append(",\"type\":\"").append(v.getClass().getSimpleName()).append("\"");
            sb.append(",\"brand\":\"").append(escape(v.getBrand())).append("\"");
            sb.append(",\"model\":\"").append(escape(v.getModel())).append("\"");
            sb.append(",\"year\":").append(v.getYear());
            if (v instanceof Car) {
                Car c = (Car) v; sb.append(",\"doors\":").append(c.getNumDoors()); sb.append(",\"fuel\":\"").append(escape(c.getFuelType())).append("\"");
            } else if (v instanceof Bike) {
                Bike b = (Bike) v; sb.append(",\"sidecar\":").append(b.hasSidecar()); sb.append(",\"category\":\"").append(escape(b.getType())).append("\"");
            } else if (v instanceof Truck) {
                Truck t = (Truck) v; sb.append(",\"payload\":").append(t.getPayloadCapacityKg()); sb.append(",\"trailer\":").append(t.hasTrailer());
            } else if (v instanceof Motorcycle) {
                Motorcycle m = (Motorcycle) v; sb.append(",\"cc\":").append(m.getEngineCc()); sb.append(",\"category\":\"").append(escape(m.getCategory())).append("\"");
            }
            sb.append("}");
        }
        sb.append("\n]");
        FileHandler.writeToFile(filename, Arrays.asList(sb.toString()));
    }

    // CSV methods removed — we now only persist in JSON format.

    public void loadFromJson(String filename) {
        List<String> lines = FileHandler.readFromFile(filename);
        if (lines.isEmpty()) return;
        String json = String.join("\n", lines);
        // naive parse: split objects
        clear();
        int idx = 0;
        while (true) {
            int start = json.indexOf('{', idx);
            if (start < 0) break;
            int end = json.indexOf('}', start);
            if (end < 0) break;
            String obj = json.substring(start+1, end);
            Map<String, String> map = new HashMap<>();
            List<String> pairs = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            boolean inQuotes = false;
            for (char ch : obj.toCharArray()) {
                if (ch == '"') inQuotes = !inQuotes;
                if (ch == ',' && !inQuotes) {
                    pairs.add(cur.toString()); cur.setLength(0);
                } else {
                    cur.append(ch);
                }
            }
            if (cur.length() > 0) pairs.add(cur.toString());
            for (String p : pairs) {
                String[] kv = p.split(":",2);
                if (kv.length < 2) continue;
                String key = kv[0].trim().replaceAll("\"", "");
                String val = kv[1].trim();
                if (val.startsWith("\"")) val = val.replaceAll("\"", "");
                map.put(key, val);
            }
            try {
                String type = map.get("type");
                String id = map.get("id");
                String brand = map.get("brand");
                String model = map.get("model");
                int year = Integer.parseInt(map.getOrDefault("year", "0"));
                switch (type.toLowerCase()) {
                    case "car":
                        int doors = Integer.parseInt(map.getOrDefault("doors", "4"));
                        String fuel = map.getOrDefault("fuel", "Petrol");
                        addVehicle(new Car(id, brand, model, year, doors, fuel));
                        break;
                    case "bike":
                        boolean hasSide = Boolean.parseBoolean(map.getOrDefault("sidecar", "false"));
                        String cat = map.getOrDefault("category", "Cruiser");
                        addVehicle(new Bike(id, brand, model, year, hasSide, cat));
                        break;
                    case "truck":
                        double payload = Double.parseDouble(map.getOrDefault("payload", "0"));
                        boolean trailer = Boolean.parseBoolean(map.getOrDefault("trailer", "false"));
                        addVehicle(new Truck(id, brand, model, year, payload, trailer));
                        break;
                    case "motorcycle":
                        int cc = Integer.parseInt(map.getOrDefault("cc", "500"));
                        String mcat = map.getOrDefault("category", "Sports");
                        addVehicle(new Motorcycle(id, brand, model, year, cc, mcat));
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error parsing JSON object: " + e.getMessage());
            }
            idx = end + 1;
        }
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }

    /**
     * Get vehicle by id
     */
    public Vehicle getVehicleById(String id) {
        return vehicles.stream().filter(v -> v.getId().equals(id)).findFirst().orElse(null);
    }
}
