package models;

/**
 * Demonstrates multiple inheritance (extends Car, implements Drivable)
 */
public class DrivableCar extends Car implements Drivable {
    private int currentSpeed = 0;
    
    public DrivableCar(String brand, String model, int year, int numDoors, String fuelType) {
        super(brand, model, year, numDoors, fuelType);
    }

    public DrivableCar(String id, String brand, String model, int year, int numDoors, String fuelType) {
        super(id, brand, model, year, numDoors, fuelType);
    }
    
    @Override
    public void accelerate() {
        if (currentSpeed < (int) getMaxSpeed()) {
            currentSpeed += 20;
            System.out.println("Accelerating... Current speed: " + currentSpeed + " km/h");
        }
    }
    
    @Override
    public void brake() {
        if (currentSpeed > 0) {
            currentSpeed -= 20;
            System.out.println("Braking... Current speed: " + currentSpeed + " km/h");
        }
    }
    
    @Override
    public void turnLeft() {
        System.out.println("Turning left...");
    }
    
    @Override
    public void turnRight() {
        System.out.println("Turning right...");
    }
    
    public int getCurrentSpeed() {
        return currentSpeed;
    }
}
