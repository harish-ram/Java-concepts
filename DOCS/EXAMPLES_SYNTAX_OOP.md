# Java Examples: Syntax, Data Types & OOP

This file contains compact, copy-paste-ready examples that map directly to concepts demonstrated in the project: syntax & data types, classes & objects, encapsulation (getters/setters), inheritance & polymorphism, interfaces, and a short stream example.

> File references in the repo: `src/models/*`, `src/main/Main.java`, `src/utilities/DataProcessor.java`, `src/services/VehicleService.java`.

---

## 1) Syntax & Data Types 🔧
File reference: `src/main/Main.java` / `src/utilities/DataProcessor.java`

```java
// primitives, String, control flow, method
int year = 2023;
double price = 19999.95;
String brand = "Toyota";

public static boolean isRecent(int year) {
    if (year >= 2020) return true;
    return false;
}
```

---

## 2) Class, Fields, Constructor, Getters/Setters (Encapsulation) 🔒
File reference: `src/models/Vehicle.java`

```java
public class Vehicle {
    private String id;
    private String brand;
    private int manufactureYear;

    public Vehicle(String id, String brand, int manufactureYear) {
        this.id = id;
        this.brand = brand;
        this.manufactureYear = manufactureYear;
    }

    public String getId() { return id; }
    public String getBrand() { return brand; }
    public int getManufactureYear() { return manufactureYear; }

    public void setBrand(String brand) { this.brand = brand; }
}
```

---

## 3) Inheritance & Polymorphism ⚙️
File reference: `src/models/Car.java` (extends Vehicle)

```java
public class Car extends Vehicle {
    private int doors;

    public Car(String id, String brand, int year, int doors) {
        super(id, brand, year);
        this.doors = doors;
    }

    @Override
    public String toString() {
        return getBrand() + " " + getManufactureYear() + " (" + doors + " doors)";
    }

    public void start() {
        System.out.println("Car starting...");
    }
}

// Polymorphic usage:
Vehicle v = new Car("c1","Honda",2021,4);
System.out.println(v.toString()); // invokes Car.toString()
```

---

## 4) Interface (contract) and Implementation 📐
File reference: `src/models/Drivable.java` and implementers

```java
public interface Drivable {
    void start();
    void stop();
}

public class Motorcycle extends Vehicle implements Drivable {
    public Motorcycle(String id, String brand, int year) {
        super(id, brand, year);
    }
    @Override public void start() { System.out.println("Motorcycle started"); }
    @Override public void stop()  { System.out.println("Motorcycle stopped"); }
}
```

---

## 5) Collections & Streams (practical usage) 📚
File reference: `src/main/Main.java`, `src/services/VehicleService.java`

```java
List<Vehicle> list = List.of(
    new Car("c1","Toyota",2022,4),
    new Car("c2","Toyota",2019,4),
    new Motorcycle("m1","Yamaha",2023)
);

// filter for recent vehicles
List<Vehicle> recent = list.stream()
                            .filter(v -> v.getManufactureYear() >= 2020)
                            .collect(Collectors.toList());
```

---

## Want this added to the main overview?
If you'd like, I can link this file from `DOCS/DETAILED_OVERVIEW.md` and/or add inline annotations to the actual source files to point to these examples.

---

*Created: EXAMPLES_SYNTAX_OOP.md*