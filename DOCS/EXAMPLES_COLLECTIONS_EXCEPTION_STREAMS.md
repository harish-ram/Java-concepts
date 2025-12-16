# Examples: ArrayList, HashMap, Exception Handling & Streams

This document contains concise examples and references from the project that demonstrate ArrayList, HashMap, exception handling (including try-with-resources), and Java Streams.

> File pointers: `src/data/VehicleDatabase.java`, `src/utilities/DataProcessor.java`, `src/services/VehicleService.java`, `src/main/Main.java`.

---

## ArrayList ✅
File reference: `src/data/VehicleDatabase.java`

```java
 1: // create and manipulate an ArrayList of Vehicles
 2: List<Vehicle> vehicles = new ArrayList<>();
 3: vehicles.add(new Car("c1","Toyota",2022,4));
 4: vehicles.add(new Motorcycle("m1","Yamaha",2023));
 5: 
 6: for (int i = 0; i < vehicles.size(); i++) {
 7:     Vehicle v = vehicles.get(i);
 8:     System.out.println(v.getId() + ": " + v.getBrand());
 9: }
10: // enhanced for
11: for (Vehicle v : vehicles) {
12:     System.out.println(v);
13: }
```

---

## HashMap (counts/grouping) ✅
File reference: `src/services/VehicleService.java`

```java
1: // count vehicles per brand using a HashMap
2: Map<String, Integer> counts = new HashMap<>();
3: for (Vehicle v : vehicles) {
4:     counts.put(v.getBrand(), counts.getOrDefault(v.getBrand(), 0) + 1);
5: }
6: 
7: // or using streams (see Streams section)
```

---

## Exception Handling & try-with-resources ✅
File reference: `src/utilities/FileHandler.java`, `src/utilities/DataProcessor.java`

```java
 1: // checked exception handling and try-with-resources for I/O
 2: try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
 3:     String line;
 4:     while ((line = br.readLine()) != null) {
 5:         // process
 6:     }
 7: } catch (IOException e) {
 8:     System.err.println("Error reading file: " + e.getMessage());
 9: }
10: 
11: // throw IllegalArgumentException for invalid inputs
12: public void setManufactureYear(int year) {
13:     if (year < 1886) throw new IllegalArgumentException("Invalid year");
14:     this.manufactureYear = year;
15: }
```

---

## Streams (filter/map/collect/grouping) ✅
File reference: `src/main/Main.java`, `src/services/VehicleService.java`

```java
 1: List<Vehicle> list = // ...
 2: // filter and collect
 3: List<Vehicle> recent = list.stream()
 4:     .filter(v -> v.getManufactureYear() >= 2020)
 5:     .collect(Collectors.toList());
 6: 
 7: // group by brand
 8: Map<String, List<Vehicle>> grouped = list.stream()
 9:     .collect(Collectors.groupingBy(Vehicle::getBrand));
10: 
11: // count per brand
12: Map<String, Long> counts = list.stream()
13:     .collect(Collectors.groupingBy(Vehicle::getBrand, Collectors.counting()));
14: 
15: // map and join
16: String brands = list.stream()
17:     .map(Vehicle::getBrand)
18:     .distinct()
19:     .collect(Collectors.joining(", "));
```

---

*Created: EXAMPLES_COLLECTIONS_EXCEPTION_STREAMS.md*