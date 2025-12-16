package models;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Motorcycle model with engine capacity and category
 */
@Entity
@DiscriminatorValue("MOTORCYCLE")
public class Motorcycle extends Vehicle {
    @Column(name = "engine_cc")
    private int engineCc;
    private String category; // "Sports", "Touring", "Cruiser"

    // Required by JPA
    protected Motorcycle() {}

    public Motorcycle(String brand, String model, int year, int engineCc, String category) {
        super(brand, model, year);
        this.engineCc = engineCc;
        this.category = category;
    }

    public Motorcycle(String id, String brand, String model, int year, int engineCc, String category) {
        super(id, brand, model, year);
        this.engineCc = engineCc;
        this.category = category;
    }

    @Override
    public void start() {
        System.out.println("Motorcycle engine started");
    }

    @Override
    public void stop() {
        System.out.println("Motorcycle engine stopped");
    }

    @Override
    public double getMaxSpeed() {
        // rough estimate based on engineCc
        return 220.0 + (engineCc / 100.0);
    }

    public int getEngineCc() {
        return engineCc;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("  Type: Motorcycle | Engine: " + engineCc + "cc | Category: " + category);
    }
}
