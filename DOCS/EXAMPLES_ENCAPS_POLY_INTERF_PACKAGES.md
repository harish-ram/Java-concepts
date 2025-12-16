# Examples: Encapsulation, Polymorphism, Interfaces & Packages

This document contains concise examples and references from the project that demonstrate Encapsulation, Polymorphism, Interfaces, and Packages. Use these to study or present the concepts.

> File pointers: `src/models/Vehicle.java`, `src/models/Car.java`, `src/models/Motorcycle.java`, `src/models/Drivable.java`, `src/data/VehicleRepository.java`, `src/main/Main.java`.

---

## Encapsulation 🔒
File reference: `src/models/Vehicle.java`

Encapsulation hides internal state (private fields) and exposes controlled access via getters/setters. Setters may validate inputs.

```java
 1: public class Vehicle {
 2:     private String id;
 3:     private String brand;
 4:     private int manufactureYear;
 5: 
 6:     public Vehicle(String id, String brand, int manufactureYear) {
 7:         this.id = id;
 8:         setManufactureYear(manufactureYear);
 9:         this.brand = brand;
10:     }
11: 
12:     public int getManufactureYear() { return manufactureYear; }
13: 
14:     public void setManufactureYear(int year) {
15:         if (year < 1886) throw new IllegalArgumentException("Invalid manufacture year");
16:         this.manufactureYear = year;
17:     }
18: }
```

---

## Polymorphism ⚙️
File reference: `src/models/Car.java`, `src/models/Motorcycle.java`, `src/main/Main.java`

Polymorphism allows code to work with superclass or interface types while concrete subclasses provide behavior (overrides).

```java
1: Vehicle v1 = new Car("c1","Ford",2020,4);
2: Vehicle v2 = new Motorcycle("m1","Harley",2021);
3: 
4: List<Vehicle> vehicles = Arrays.asList(v1, v2);
5: for (Vehicle v : vehicles) {
6:     System.out.println(v); // runtime dispatch to each class' toString()
7: }
```

---

## Interfaces 📐
File reference: `src/models/Drivable.java`, `src/data/VehicleRepository.java`

Interfaces define contracts. Multiple classes can implement the same interface and be used interchangeably.

```java
 1: public interface Drivable {
 2:     void start();
 3:     void stop();
 4: }
 5: 
 6: public interface VehicleRepository {
 7:     void addVehicle(Vehicle v);
 8:     Optional<Vehicle> findById(String id);
 9: }
10: 
11: // implementations: Motorcycle implements Drivable; VehicleRepositoryJpa/Jdbc implement VehicleRepository
```

---

## Packages and organization 📦
Examples in the repo: top-level packages `models`, `data`, `services`, `web`, `utilities`.

```java
 1: // at top of the file
 2: package models;
 3: 
 4: // usage in other packages
 5: import models.Car;
 6: 
 7: public class Demo {
 8:     public static void main(String[] args) {
 9:         Car c = new Car("c1","Toyota",2022,4);
10:     }
11: }
```

Notes:
- Package declaration must be the first non-comment line in a Java source file.
- Files are stored in matching folder paths (e.g., `src/models/Car.java`).

---

*Created: EXAMPLES_ENCAPS_POLY_INTERF_PACKAGES.md*