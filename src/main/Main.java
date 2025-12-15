package main;

import concurrency.*;
import data.*;
import java.util.*;
import models.*;
import utilities.*;

/**
 * Main application demonstrating all Java learning concepts
 */
public class Main {
    
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("    Java Learning Project");
        System.out.println("========================================");
        
        // parse args early so we can skip demos when running as a server or GUI in dev
        boolean runDemos = true;
        boolean startServer = false, startGui = false, useJdbc = false, startH2Console = false;
        int port = 8000;
        String jdbcUrl = "jdbc:h2:mem:vehicledb;DB_CLOSE_DELAY=-1";
        String dbUser = "sa";
        String dbPass = "";
        boolean createDb = false;
        String adminToken = System.getenv("ADMIN_TOKEN");
        for (String a : args) {
            if (a.equalsIgnoreCase("--server")) startServer = true;
            if (a.equalsIgnoreCase("--gui")) startGui = true;
            if (a.equalsIgnoreCase("--jdbc")) useJdbc = true;
            if (a.equalsIgnoreCase("--no-demo")) runDemos = false;
            if (a.equalsIgnoreCase("--run-demos")) runDemos = true;
            if (a.startsWith("--port=")) port = Integer.parseInt(a.substring("--port=".length()));
            if (a.startsWith("--jdbcUrl=")) jdbcUrl = a.substring("--jdbcUrl=".length());
            if (a.startsWith("--dbUser=")) dbUser = a.substring("--dbUser=".length());
            if (a.startsWith("--dbPass=")) dbPass = a.substring("--dbPass=".length());
            if (a.equalsIgnoreCase("--create-db")) createDb = true;
            if (a.startsWith("--admin-token=")) adminToken = a.substring("--admin-token=".length());
            if (a.equalsIgnoreCase("--start-h2")) startH2Console = true;
        }

        // Optional: start server and/or GUI with service-backed repository
        // demos only when runDemos is true and server/gui not requested
        if (runDemos && !startServer && !startGui) {
            // 1. OOP: Inheritance, Polymorphism, Abstraction
            demonstrateOOP();
            
            // 2. Collections, Generics, Lambdas, Streams
            demonstrateCollectionsAndStreams();
            
            // 3. File I/O and Exception Handling
            demonstrateFileIO();
            
            // 4. Concurrency
            demonstrateConcurrency();

            // 5. Interactive User Input Demo (ArrayList, HashMap, Exception Handling, Streams), with persistence
            interactiveVehicleDemo();
        }
        for (String a : args) {
            if (a.equalsIgnoreCase("--server")) startServer = true;
            if (a.equalsIgnoreCase("--gui")) startGui = true;
            if (a.equalsIgnoreCase("--jdbc")) useJdbc = true;
            if (a.startsWith("--port=")) port = Integer.parseInt(a.substring("--port=".length()));
            if (a.startsWith("--jdbcUrl=")) jdbcUrl = a.substring("--jdbcUrl=".length());
            if (a.startsWith("--dbUser=")) dbUser = a.substring("--dbUser=".length());
            if (a.startsWith("--dbPass=")) dbPass = a.substring("--dbPass=".length());
            if (a.equalsIgnoreCase("--start-h2")) startH2Console = true;
        }

        // If user requested JDBC and did not set a URL, default to a file-backed DB in project root for dev.
        if (useJdbc && (jdbcUrl == null || jdbcUrl.isEmpty() || jdbcUrl.contains("mem:vehicledb"))) {
            jdbcUrl = "jdbc:h2:./vehicledb;DB_CLOSE_DELAY=-1";
        }
        // fallback to environment variables if explicit credentials not set
        if ((dbUser == null || dbUser.isEmpty()) && System.getenv("DB_USER") != null) dbUser = System.getenv("DB_USER");
        if ((dbPass == null || dbPass.isEmpty()) && System.getenv("DB_PASS") != null) dbPass = System.getenv("DB_PASS");
        data.VehicleRepository repo = startServer && useJdbc ? new data.VehicleDaoJdbc(jdbcUrl, dbUser, dbPass) : new data.VehicleDatabaseRepository();
        services.VehicleService service = new services.VehicleService(repo);
        if (startServer) {
            // Optionally start the H2 console to inspect the DB
            if (startH2Console) {
                try {
                    // Start H2 console in a separate process (dev-only)
                    final String h2Jar = "lib/h2-2.1.214.jar";
                    final List<String> cmd = new ArrayList<>();
                    cmd.add("java");
                    cmd.add("-cp");
                    cmd.add(h2Jar);
                    cmd.add("org.h2.tools.Server");
                    cmd.add("-tcp");
                    cmd.add("-web");
                    cmd.add("-tcpAllowOthers");
                    new ProcessBuilder(cmd).inheritIO().start();
                } catch (Exception e) {
                    System.err.println("Failed to start H2 console: " + e.getMessage());
                }
            }
            web.Server srv = new web.Server(port, repo, adminToken);
            // optionally create DB schema if requested
            if (useJdbc && createDb) {
                try { repo.init(); System.out.println("DB schema ensured (create-db)"); } catch (Exception ex) { System.err.println("Error creating DB schema: " + ex.getMessage()); }
            }
            srv.start();
            try {
                System.out.println("Server running. Waiting for shutdown (use /api/admin/shutdown)...");
                srv.waitForStop();
            } catch (InterruptedException ie) {
                // ignore
            }
        }
        if (startGui) {
            final services.VehicleService svc = service;
            javax.swing.SwingUtilities.invokeLater(() -> { gui.VehicleGUI gui = new gui.VehicleGUI(svc); gui.setVisible(true); });
        }
        
        System.out.println("\n========================================");
        System.out.println("    Application Complete");
        System.out.println("========================================");
    }
        /**
         * Interactive demo: get vehicle details from user, store in ArrayList, group by brand with HashMap, filter with streams, handle exceptions
         */
        private static void interactiveVehicleDemo() {
            System.out.println("\n========== INTERACTIVE VEHICLE DEMO ==========");
            VehicleDatabase db = new VehicleDatabase();
            // Load from persistence
            db.loadFromJson("vehicles.json");
            ArrayList<Vehicle> vehicles = new ArrayList<>(db.getAllVehicles());
            HashMap<String, List<Vehicle>> brandMap = new HashMap<>(db.groupByBrand());
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("How many vehicles do you want to add? ");
                int count = Integer.parseInt(scanner.nextLine());
                for (int i = 0; i < count; i++) {
                    System.out.println("\nEnter details for vehicle " + (i+1) + ":");
                    System.out.print("Type (car/bike): ");
                    String type = scanner.nextLine().trim().toLowerCase();
                    System.out.print("Brand: ");
                    String brand = scanner.nextLine().trim();
                    System.out.print("Model: ");
                    String model = scanner.nextLine().trim();
                    System.out.print("Year: ");
                    int year;
                    try {
                        year = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException nfe) {
                        System.out.println("Invalid year, skipping vehicle.");
                        continue;
                    }
                    if (type.equals("car")) {
                        System.out.print("Num doors: ");
                        int doors;
                        try {
                            doors = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid doors, skipping vehicle.");
                            continue;
                        }
                        System.out.print("Fuel type: ");
                        String fuel = scanner.nextLine().trim();
                        Car c = new Car(brand, model, year, doors, fuel);
                        vehicles.add(c);
                        brandMap.computeIfAbsent(brand, k -> new ArrayList<>()).add(c);
                    } else if (type.equals("bike")) {
                        System.out.print("Has sidecar (true/false): ");
                        boolean sidecar;
                        try {
                            sidecar = Boolean.parseBoolean(scanner.nextLine());
                        } catch (Exception ex) {
                            System.out.println("Invalid boolean, skipping vehicle.");
                            continue;
                        }
                        System.out.print("Bike type (Cruiser/Sports/Touring): ");
                        String bikeType = scanner.nextLine().trim();
                        Bike b = new Bike(brand, model, year, sidecar, bikeType);
                        vehicles.add(b);
                        brandMap.computeIfAbsent(brand, k -> new ArrayList<>()).add(b);
                    } else if (type.equals("truck")) {
                        System.out.print("Payload capacity (kg): ");
                        double payload;
                        try {
                            payload = Double.parseDouble(scanner.nextLine());
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid payload, skipping vehicle.");
                            continue;
                        }
                        System.out.print("Has trailer (true/false): ");
                        boolean trailer;
                        try {
                            trailer = Boolean.parseBoolean(scanner.nextLine());
                        } catch (Exception ex) {
                            System.out.println("Invalid boolean, skipping vehicle.");
                            continue;
                        }
                        Truck t = new Truck(brand, model, year, payload, trailer);
                        vehicles.add(t);
                        brandMap.computeIfAbsent(brand, k -> new ArrayList<>()).add(t);
                    } else if (type.equals("motorcycle")) {
                        System.out.print("Engine CC: ");
                        int cc;
                        try {
                            cc = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid engine CC, skipping vehicle.");
                            continue;
                        }
                        System.out.print("Category (Sports/Touring/Cruiser): ");
                        String mcat = scanner.nextLine().trim();
                        Motorcycle m = new Motorcycle(brand, model, year, cc, mcat);
                        vehicles.add(m);
                        brandMap.computeIfAbsent(brand, k -> new ArrayList<>()).add(m);
                    } else {
                        System.out.println("Unknown type, skipping...");
                        continue;
                    }
                }
                // Persist new vehicles back to JSON
                // Clear and add all to DB then save
                db.clear();
                vehicles.forEach(db::addVehicle);
                db.saveToJson("vehicles.json");

                // Use streams to filter by year
                System.out.print("\nEnter a year to filter vehicles: ");
                try {
                    int filterYear = Integer.parseInt(scanner.nextLine());
                    System.out.println("Vehicles from year " + filterYear + ":");
                    vehicles.stream()
                        .filter(v -> v.getYear() == filterYear)
                        .forEach(Vehicle::displayInfo);
                } catch (Exception exFilter) {
                    System.out.println("Invalid year input.");
                }

                // Show vehicles grouped by brand using HashMap
                System.out.println("\nVehicles grouped by brand:");
                brandMap.forEach((brand, list) -> {
                    System.out.println("Brand: " + brand);
                    list.forEach(v -> System.out.println("  - " + v.getModel()));
                });

            } catch (Exception e) {
                // Keep a generic catch for unexpected exceptions, but print message
                System.out.println("Error: " + e.getMessage());
            }
            
            
        }
    
    /**
     * Demonstrates OOP concepts: Inheritance, Polymorphism, Interfaces
     */
    private static void demonstrateOOP() {
        System.out.println("\n========== OOP DEMO ==========");
        
        // Create vehicles using polymorphism
        Vehicle car = new Car("Toyota", "Camry", 2023, 4, "Hybrid");
        Vehicle bike = new Bike("Harley-Davidson", "Street 750", 2022, false, "Cruiser");
        
        // Polymorphic behavior
        System.out.println("\n--- Starting vehicles ---");
        car.start();
        bike.start();
        
        // Display information
        System.out.println("\n--- Vehicle Information ---");
        car.displayInfo();
        bike.displayInfo();
        
        // Max speeds
        System.out.println("\n--- Max Speeds ---");
        System.out.println("Car max speed: " + car.getMaxSpeed() + " km/h");
        System.out.println("Bike max speed: " + bike.getMaxSpeed() + " km/h");
        
        // Interface implementation
        System.out.println("\n--- Drivable Interface ---");
        DrivableCar drivableCar = new DrivableCar("Honda", "Civic", 2023, 4, "Petrol");
        drivableCar.start();
        drivableCar.accelerate();
        drivableCar.accelerate();
        drivableCar.turnLeft();
        drivableCar.brake();
        drivableCar.stop();
    }
    
    /**
     * Demonstrates Collections, Generics, Lambdas, and Streams
     */
    private static void demonstrateCollectionsAndStreams() {
        System.out.println("\n========== COLLECTIONS & STREAMS DEMO ==========");
        
        // Collections demo
        DataProcessor.demonstrateCollections();
        
        // Lambdas demo
        DataProcessor.demonstrateLambdas();
        
        // Streams demo
        DataProcessor.demonstrateStreams();
        
        // Vehicle database using collections
        System.out.println("\n--- Vehicle Database Demo ---");
        VehicleDatabase db = new VehicleDatabase();
        
        // Add vehicles
        db.addVehicle(new Car("Toyota", "Camry", 2023, 4, "Hybrid"));
        db.addVehicle(new Car("Honda", "Civic", 2022, 4, "Petrol"));
        db.addVehicle(new Car("Tesla", "Model 3", 2023, 4, "Electric"));
        db.addVehicle(new Bike("Harley-Davidson", "Street 750", 2022, false, "Cruiser"));
        db.addVehicle(new Bike("Honda", "CB500F", 2023, false, "Sports"));
        db.addVehicle(new Truck("Ford", "F-150", 2021, 1500.0, false));
        db.addVehicle(new Motorcycle("Yamaha", "R1", 2022, 1000, "Sports"));
        
        // Display all
        db.displayAllVehicles();
        
        // Streams operations
        System.out.println("\n--- Stream Operations ---");
        db.countByType();
        System.out.println("Total vehicles: " + db.getTotalCount());
        System.out.println("Average max speed: " + String.format("%.2f", db.getAverageMaxSpeed()) + " km/h");
        
        Vehicle fastest = db.getFastestVehicle();
        if (fastest != null) {
            System.out.println("Fastest vehicle: " + fastest.getBrand() + " " + fastest.getModel());
        }
        
        // Filter by brand
        System.out.println("\nHonda vehicles:");
        db.getVehiclesByBrand("Honda").forEach(v -> System.out.println("  - " + v.getModel()));
    }
    
    /**
     * Demonstrates File I/O and Exception Handling
     */
    private static void demonstrateFileIO() {
        System.out.println("\n========== FILE I/O DEMO ==========");
        
        String filename = "vehicle_data.txt";
        
        // Write to file
        System.out.println("\n--- Writing to File ---");
        List<String> data = Arrays.asList(
            "Toyota Camry - 2023",
            "Honda Civic - 2022",
            "Harley-Davidson Street 750 - 2022"
        );
        FileHandler.writeToFile(filename, data);
        
        // Check file properties
        FileHandler.checkFileProperties(filename);
        
        // Read from file
        System.out.println("\n--- Reading from File ---");
        List<String> readData = FileHandler.readFromFile(filename);
        System.out.println("Contents: " + readData);
        
        // Append to file
        System.out.println("\n--- Appending to File ---");
        FileHandler.appendToFile(filename, "Tesla Model 3 - 2023");
        
        // Read again
        readData = FileHandler.readFromFile(filename);
        System.out.println("Updated contents: " + readData);
        
        // Delete file
        System.out.println("\n--- Deleting File ---");
        FileHandler.deleteFile(filename);
    }
    
    /**
     * Demonstrates Concurrency concepts
     */
    private static void demonstrateConcurrency() throws Exception {
        System.out.println("\n========== CONCURRENCY DEMO ==========");
        
        // ExecutorService
        ConcurrencyDemo.demonstrateExecutorService();
        
        // CompletableFuture
        ConcurrencyDemo.demonstrateCompletableFuture();
        
        // Thread-safe counter
        ConcurrencyDemo.demonstrateThreadSafeCounter();
    }
}
