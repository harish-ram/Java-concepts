package models;

/**
 * Concrete class demonstrating inheritance
 */
public class Bike extends Vehicle {
    private final boolean hasSidecar;
    private final String type; // "Cruiser", "Sports", "Touring"
    
    public Bike(String brand, String model, int year, boolean hasSidecar, String type) {
        super(brand, model, year);
        this.hasSidecar = hasSidecar;
        this.type = type;
    }
    
    @Override
    public void start() {
        System.out.println("Bike engine started (throttle up!)");
    }
    
    @Override
    public void stop() {
        System.out.println("Bike engine stopped");
    }
    
    @Override
    public double getMaxSpeed() {
        return 300.0; // km/h
    }
    
    public boolean hasSidecar() {
        return hasSidecar;
    }
    
    public String getType() {
        return type;
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  Type: Bike | Sidecar: " + hasSidecar + " | Category: " + type);
    }
}
