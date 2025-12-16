package models;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Concrete class demonstrating inheritance
 */
@Entity
@DiscriminatorValue("BIKE")
public class Bike extends Vehicle {
    @Column(name = "has_sidecar")
    private boolean hasSidecar;
    @Column(name = "bike_type")
    private String type; // "Cruiser", "Sports", "Touring"

    // Required by JPA
    protected Bike() {}

    public Bike(String brand, String model, int year, boolean hasSidecar, String type) {
        super(brand, model, year);
        this.hasSidecar = hasSidecar;
        this.type = type;
    }

    public Bike(String id, String brand, String model, int year, boolean hasSidecar, String type) {
        super(id, brand, model, year);
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
