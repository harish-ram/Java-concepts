# Examples: ArrayList, HashMap, Exception Handling & Streams

This document contains concise examples and references from the project that demonstrate ArrayList, HashMap, exception handling (including try-with-resources), and Java Streams.

> File pointers: `src/data/VehicleDatabase.java`, `src/utilities/DataProcessor.java`, `src/services/VehicleService.java`, `src/main/Main.java`.

---

## ArrayList ✅
File reference: `src/data/VehicleDatabase.java`

```java
// create and manipulate an ArrayList of Vehicles
List<Vehicle> vehicles = new ArrayList<>();
vehicles.add(new Car("c1","Toyota",2022,4));
vehicles.add(new Motorcycle("m1","Yamaha",2023));

for (int i = 0; i < vehicles.size(); i++) {
    Vehicle v = vehicles.get(i);
    System.out.println(v.getId() + ": " + v.getBrand());
}
// enhanced for
for (Vehicle v : vehicles) {
    System.out.println(v);
}
```

---

## HashMap (counts/grouping) ✅
File reference: `src/services/VehicleService.java`

```java
// count vehicles per brand using a HashMap
Map<String, Integer> counts = new HashMap<>();
for (Vehicle v : vehicles) {
    counts.put(v.getBrand(), counts.getOrDefault(v.getBrand(), 0) + 1);
}

// or using streams (see Streams section)
```

---

## Exception Handling & try-with-resources ✅
File reference: `src/utilities/FileHandler.java`, `src/utilities/DataProcessor.java`

```java
// checked exception handling and try-with-resources for I/O
try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
    String line;
    while ((line = br.readLine()) != null) {
        // process
    }
} catch (IOException e) {
    System.err.println("Error reading file: " + e.getMessage());
}

// throw IllegalArgumentException for invalid inputs
public void setManufactureYear(int year) {
    if (year < 1886) throw new IllegalArgumentException("Invalid year");
    this.manufactureYear = year;
}
```

---

## Streams (filter/map/collect/grouping) ✅
File reference: `src/main/Main.java`, `src/services/VehicleService.java`

```java
List<Vehicle> list = // ...
// filter and collect
List<Vehicle> recent = list.stream()
    .filter(v -> v.getManufactureYear() >= 2020)
    .collect(Collectors.toList());

// group by brand
Map<String, List<Vehicle>> grouped = list.stream()
    .collect(Collectors.groupingBy(Vehicle::getBrand));

// count per brand
Map<String, Long> counts = list.stream()
    .collect(Collectors.groupingBy(Vehicle::getBrand, Collectors.counting()));

// map and join
String brands = list.stream()
    .map(Vehicle::getBrand)
    .distinct()
    .collect(Collectors.joining(", "));
```

---

*Created: EXAMPLES_COLLECTIONS_EXCEPTION_STREAMS.md*