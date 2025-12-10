package models;

/**
 * Concrete class demonstrating inheritance and polymorphism
 */
public class Car extends Vehicle {
    private final int numDoors;
    private final String fuelType; // "Petrol", "Diesel", "Electric", "Hybrid"
    
    public Car(String brand, String model, int year, int numDoors, String fuelType) {
        super(brand, model, year);
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
