package models;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Concrete class demonstrating inheritance and polymorphism
 */
@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {
    @Column(name = "num_doors")
    private int numDoors;
    @Column(name = "fuel_type")
    private String fuelType; // "Petrol", "Diesel", "Electric", "Hybrid"

    // Required by JPA
    protected Car() {}

    public Car(String brand, String model, int year, int numDoors, String fuelType) {
        super(brand, model, year);
        this.numDoors = numDoors;
        this.fuelType = fuelType;
    }

    // Constructor with explicit id for preserving ids on update
    public Car(String id, String brand, String model, int year, int numDoors, String fuelType) {
        super(id, brand, model, year);
        this.numDoors = numDoors;
        this.fuelType = fuelType;
    }
    
    @Override
    public void start() {
        System.out.println("Car engine started with " + fuelType + " fuel");
    }
    
    @Override
    public void stop() {
        System.out.println("Car engine stopped");
    }
    
    @Override
    public double getMaxSpeed() {
        return 250.0; // km/h
    }
    
    public int getNumDoors() {
        return numDoors;
    }
    
    public String getFuelType() {
        return fuelType;
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  Type: Car | Doors: " + numDoors + " | Fuel: " + fuelType);
    }
}
