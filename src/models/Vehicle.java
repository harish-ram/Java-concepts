package models;

/**
 * JPA entity base class for vehicles. Uses single-table inheritance.
 */
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

@Entity
@Table(name = "vehicles")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type")
public abstract class Vehicle {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "brand")
    private String brand;

    @Column(name = "model")
    private String model;

    @Column(name = "manufacture_year")
    protected int year;

    // Required by JPA
    protected Vehicle() {}

    public Vehicle(String brand, String model, int year) {
        this(UUID.randomUUID().toString(), brand, model, year);
    }

    // Constructor with explicit id (for updates/persistence)
    protected Vehicle(String id, String brand, String model, int year) {
        this.id = id;
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

    public String getId() {
        return id;
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
