# Java Examples: Syntax, Data Types & OOP

This file contains compact, copy-paste-ready examples that map directly to concepts demonstrated in the project: syntax & data types, classes & objects, encapsulation (getters/setters), inheritance & polymorphism, interfaces, and a short stream example.

> File references in the repo: `src/models/*`, `src/main/Main.java`, `src/utilities/DataProcessor.java`, `src/services/VehicleService.java`.

---

## 1) Syntax & Data Types 🔧
File reference: `src/main/Main.java` / `src/utilities/DataProcessor.java`

```java
1: // primitives, String, control flow, method
2: int year = 2023;
3: double price = 19999.95;
4: String brand = "Toyota";
5: 
6: public static boolean isRecent(int year) {
7:     if (year >= 2020) return true;
8:     return false;
9: }
```

---

## 2) Class, Fields, Constructor, Getters/Setters (Encapsulation) 🔒
File reference: `src/models/Vehicle.java`

```java
 1: public class Vehicle {
 2:     private String id;
 3:     private String brand;
 4:     private int manufactureYear;
 5: 
 6:     public Vehicle(String id, String brand, int manufactureYear) {
 7:         this.id = id;
 8:         this.brand = brand;
 9:         this.manufactureYear = manufactureYear;
10:     }
11: 
12:     public String getId() { return id; }
13:     public String getBrand() { return brand; }
14:     public int getManufactureYear() { return manufactureYear; }
15: 
16:     public void setBrand(String brand) { this.brand = brand; }
17: }
```

---

## 3) Inheritance & Polymorphism ⚙️
File reference: `src/models/Car.java` (extends Vehicle)

```java
 1: public class Car extends Vehicle {
 2:     private int doors;
 3: 
 4:     public Car(String id, String brand, int year, int doors) {
 5:         super(id, brand, year);
 6:         this.doors = doors;
 7:     }
 8: 
 9:     @Override
10:     public String toString() {
11:         return getBrand() + " " + getManufactureYear() + " (" + doors + " doors)";
12:     }
13: 
14:     public void start() {
15:         System.out.println("Car starting...");
16:     }
17: }
18: 
19: // Polymorphic usage:
20: Vehicle v = new Car("c1","Honda",2021,4);
21: System.out.println(v.toString()); // invokes Car.toString()
```

---

## 4) Interface (contract) and Implementation 📐
File reference: `src/models/Drivable.java` and implementers

```java
 1: public interface Drivable {
 2:     void start();
 3:     void stop();
 4: }
 5: 
 6: public class Motorcycle extends Vehicle implements Drivable {
 7:     public Motorcycle(String id, String brand, int year) {
 8:         super(id, brand, year);
 9:     }
10:     @Override public void start() { System.out.println("Motorcycle started"); }
11:     @Override public void stop()  { System.out.println("Motorcycle stopped"); }
12: }
```

---

## 5) Collections & Streams (practical usage) 📚
File reference: `src/main/Main.java`, `src/services/VehicleService.java`

```java
 1: List<Vehicle> list = List.of(
 2:     new Car("c1","Toyota",2022,4),
 3:     new Car("c2","Toyota",2019,4),
 4:     new Motorcycle("m1","Yamaha",2023)
 5: );
 6: 
 7: // filter for recent vehicles
 8: List<Vehicle> recent = list.stream()
 9:                             .filter(v -> v.getManufactureYear() >= 2020)
10:                             .collect(Collectors.toList());
```

---

## Want this added to the main overview?
If you'd like, I can link this file from `DOCS/DETAILED_OVERVIEW.md` and/or add inline annotations to the actual source files to point to these examples.

---

*Created: EXAMPLES_SYNTAX_OOP.md*