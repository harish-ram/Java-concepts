package data;

import models.*;
import java.util.*;
import java.util.stream.*;

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
     */
    public List<Vehicle> getVehiclesByBrand(String brand) {
        return vehicles.stream()
                .filter(v -> v.getBrand().equalsIgnoreCase(brand))
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
        System.out.println("\nVehicle Count: Cars=" + cars + ", Bikes=" + bikes);
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
}
