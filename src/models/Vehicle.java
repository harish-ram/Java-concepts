package models;

/**
 * Abstract base class demonstrating OOP concepts:
 * - Encapsulation (private/protected fields)
 * - Inheritance (extended by Car, Bike, Truck)
 * - Abstraction (abstract methods)
 */
public abstract class Vehicle {
    private final String brand;
    private final String model;
    protected int year;
    
    public Vehicle(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
    }
    
    // Getters
    public String getBrand() {
        return brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public int getYear() {
        return year;
    }
    
    // Abstract method - must be implemented by subclasses
    public abstract void start();
    public abstract void stop();
    public abstract double getMaxSpeed();
    
    // Concrete method - shared by all vehicles
    public void displayInfo() {
        System.out.println(year + " " + brand + " " + model);
    }
}
