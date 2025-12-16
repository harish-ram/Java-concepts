# Examples: Encapsulation, Polymorphism, Interfaces & Packages

This document contains concise examples and references from the project that demonstrate Encapsulation, Polymorphism, Interfaces, and Packages. Use these to study or present the concepts.

> File pointers: `src/models/Vehicle.java`, `src/models/Car.java`, `src/models/Motorcycle.java`, `src/models/Drivable.java`, `src/data/VehicleRepository.java`, `src/main/Main.java`.

---

## Encapsulation 🔒
File reference: `src/models/Vehicle.java`

Encapsulation hides internal state (private fields) and exposes controlled access via getters/setters. Setters may validate inputs.

```java
public class Vehicle {
    private String id;
    private String brand;
    private int manufactureYear;

    public Vehicle(String id, String brand, int manufactureYear) {
        this.id = id;
        setManufactureYear(manufactureYear);
        this.brand = brand;
    }

    public int getManufactureYear() { return manufactureYear; }

    public void setManufactureYear(int year) {
        if (year < 1886) throw new IllegalArgumentException("Invalid manufacture year");
        this.manufactureYear = year;
    }
}
```

---

## Polymorphism ⚙️
File reference: `src/models/Car.java`, `src/models/Motorcycle.java`, `src/main/Main.java`

Polymorphism allows code to work with superclass or interface types while concrete subclasses provide behavior (overrides).

```java
Vehicle v1 = new Car("c1","Ford",2020,4);
Vehicle v2 = new Motorcycle("m1","Harley",2021);

List<Vehicle> vehicles = Arrays.asList(v1, v2);
for (Vehicle v : vehicles) {
    System.out.println(v); // runtime dispatch to each class' toString()
}
```

---

## Interfaces 📐
File reference: `src/models/Drivable.java`, `src/data/VehicleRepository.java`

Interfaces define contracts. Multiple classes can implement the same interface and be used interchangeably.

```java
public interface Drivable {
    void start();
    void stop();
}

public interface VehicleRepository {
    void addVehicle(Vehicle v);
    Optional<Vehicle> findById(String id);
}

// implementations: Motorcycle implements Drivable; VehicleRepositoryJpa/Jdbc implement VehicleRepository
```

---

## Packages and organization 📦
Examples in the repo: top-level packages `models`, `data`, `services`, `web`, `utilities`.

```java
// at top of the file
package models;

// usage in other packages
import models.Car;

public class Demo {
    public static void main(String[] args) {
        Car c = new Car("c1","Toyota",2022,4);
    }
}
```

Notes:
- Package declaration must be the first non-comment line in a Java source file.
- Files are stored in matching folder paths (e.g., `src/models/Car.java`).

---

*Created: EXAMPLES_ENCAPS_POLY_INTERF_PACKAGES.md*