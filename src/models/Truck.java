package models;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Truck class with a payload capacity and optional trailer
 */
@Entity
@DiscriminatorValue("TRUCK")
public class Truck extends Vehicle {
    @Column(name = "payload_capacity_kg")
    private double payloadCapacityKg;
    @Column(name = "has_trailer")
    private boolean hasTrailer;

    // Required by JPA
    protected Truck() {}

    public Truck(String brand, String model, int year, double payloadCapacityKg, boolean hasTrailer) {
        super(brand, model, year);
        this.payloadCapacityKg = payloadCapacityKg;
        this.hasTrailer = hasTrailer;
    }

    // Constructor with explicit id
    public Truck(String id, String brand, String model, int year, double payloadCapacityKg, boolean hasTrailer) {
        super(id, brand, model, year);
        this.payloadCapacityKg = payloadCapacityKg;
        this.hasTrailer = hasTrailer;
    }

    @Override
    public void start() {
        System.out.println("Truck engine started");
    }

    @Override
    public void stop() {
        System.out.println("Truck engine stopped");
    }

    @Override
    public double getMaxSpeed() {
        return 180.0; // km/h typical for heavy vehicles
    }

    public double getPayloadCapacityKg() {
        return payloadCapacityKg;
    }

    public boolean hasTrailer() {
        return hasTrailer;
    }

    public void toggleTrailer() {
        hasTrailer = !hasTrailer;
        System.out.println("Trailer detached/attached: " + hasTrailer);
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  Type: Truck | Payload: " + payloadCapacityKg + " kg | Trailer: " + hasTrailer);
    }
}
