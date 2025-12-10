# Java Learning Project

## Overview
This is a comprehensive Java learning project that demonstrates fundamental and intermediate Java concepts through practical implementations. The project is organized into multiple modules, each focusing on specific Java features and programming paradigms.

## Project Structure

```
JavaLearningProject/
├── src/
│   ├── models/           # OOP and inheritance concepts
│   ├── utilities/        # Collections, Streams, and functional programming
│   ├── concurrency/      # Threading and concurrent programming
│   ├── data/             # Database operations and stream processing
│   └── main/             # Main application entry point
├── bin/                  # Compiled class files
├── COMPILATION.txt       # Compilation and execution instructions
└── README.md            # Project documentation
```

## Module Descriptions

### 1. Models (`src/models/`)
**Focus:** Object-Oriented Programming, Inheritance, and Polymorphism

**Key Classes:**
- **Vehicle.java** - Abstract base class representing a vehicle
  - Properties: brand, model, year
  - Abstract methods: start(), stop(), getMaxSpeed()
  
- **Car.java** - Concrete implementation of Vehicle
  - Additional properties: numDoors, fuelType
  - Implements vehicle-specific behavior
  
- **Bike.java** - Concrete implementation of Vehicle
  - Additional properties: hasSidecar, type (Cruiser/Sports/Touring)
  - Demonstrates polymorphism through inheritance
  
- **Drivable.java** - Interface for driving behavior
  - Methods: accelerate(), decelerate(), turnLeft(), turnRight()
  
- **DrivableCar.java** - Implements multiple inheritance using interfaces
  - Combines Vehicle inheritance with Drivable interface
  - Demonstrates interface implementation

### 2. Utilities (`src/utilities/`)
**Focus:** Collections, Streams, Lambda Expressions, and File I/O

**Key Classes:**
- **DataProcessor.java** - Functional programming and stream operations
  - Collections demonstration (List, Set, Map)
  - Lambda expressions for sorting and filtering
  - Stream API for data transformation
  - Grouping and aggregation operations
  
- **FileHandler.java** - File I/O operations
  - File reading and writing
  - File appending
  - File properties checking
  - File deletion

### 3. Concurrency (`src/concurrency/`)
**Focus:** Multi-threading and Concurrent Programming

**Key Classes:**
- **ConcurrencyDemo.java** - Threading demonstrations
  - Thread creation and management
  - Thread synchronization
  - ExecutorService for thread pools
  - CompletableFuture for asynchronous operations
  
- **Counter.java** - Thread-safe counter implementation
  - Uses AtomicInteger for thread-safe operations
  - Demonstrates synchronization concepts

### 4. Data (`src/data/`)
**Focus:** Data Management and Stream Processing

**Key Classes:**
- **VehicleDatabase.java** - Vehicle collection management
  - Stores and manages Vehicle objects
  - Provides filtering and searching capabilities
  - Stream operations for data analysis
  - Statistics calculation (average speed, vehicle counts)

### 5. Main (`src/main/`)
**Entry Point:** Main.java
- Demonstrates all modules working together
- Shows comprehensive usage of OOP, Collections, Streams, File I/O, and Concurrency
- Provides a complete learning example from start to finish

## Key Concepts Demonstrated

### Object-Oriented Programming
- **Encapsulation** - Private fields with getter/setter methods
- **Inheritance** - Vehicle base class with Car and Bike subclasses
- **Polymorphism** - Method overriding and interface implementation
- **Abstraction** - Abstract classes and interfaces

### Collections & Streams
- **Collections** - List, Set, and Map usage
- **Streams API** - Filtering, mapping, and transforming data
- **Lambda Expressions** - Functional programming in Java
- **Collectors** - Grouping and aggregating stream data

### File I/O
- Reading and writing files
- File metadata operations
- Content appending and manipulation

### Concurrency
- Thread creation and execution
- ExecutorService for thread pool management
- CompletableFuture for asynchronous programming
- AtomicInteger for thread-safe operations

## Compilation

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- PowerShell (Windows) or Command Terminal

### Method 1: Direct Compilation (Windows PowerShell)
```powershell
cd C:\Users\ERS1399\Downloads\Full-Stack-Dev\JavaLearningProject
mkdir bin
javac -d bin src/models/*.java src/utilities/*.java src/concurrency/*.java src/data/*.java src/main/Main.java
java -cp bin main.Main
```

### Method 2: Using Gradle or Maven
For larger projects, consider using Maven or Gradle for dependency management and automated builds.

### Method 3: VS Code with Java Extension
1. Install Java Extension Pack in VS Code
2. Open the JavaLearningProject folder
3. Press `Ctrl+Shift+B` to build
4. Press `Ctrl+F5` to run

## Execution

After successful compilation, run the application:
```powershell
java -cp bin main.Main
```

### Expected Output
The application demonstrates:
1. **OOP Demo** - Vehicle creation, polymorphism, and interface implementation
2. **Collections & Streams Demo** - Data processing with modern Java APIs
3. **File I/O Demo** - Reading, writing, and managing files
4. **Concurrency Demo** - Multi-threaded operations and asynchronous programming

## Learning Outcomes

Upon completing this project, you will understand:
- ✅ Class hierarchies and inheritance patterns
- ✅ Interface-based design
- ✅ Collection frameworks and when to use them
- ✅ Stream API for functional-style operations
- ✅ Lambda expressions and functional interfaces
- ✅ File I/O operations in Java
- ✅ Thread creation and management
- ✅ Thread-safe operations with atomic classes
- ✅ ExecutorService for thread pooling
- ✅ CompletableFuture for asynchronous programming

## Project Highlights

### Code Quality
- ✅ All fields marked as `final` where appropriate
- ✅ Proper encapsulation with private fields
- ✅ Clear separation of concerns
- ✅ Comprehensive documentation with JavaDoc comments

### Best Practices
- ✅ Uses immutable fields for thread safety
- ✅ Proper resource management in File I/O
- ✅ Exception handling in file operations
- ✅ Thread-safe operations using AtomicInteger

### Testing
The main application serves as a comprehensive test suite, demonstrating all features:
- Vehicle creation and polymorphism
- Stream operations on collections
- File I/O with error handling
- Multi-threaded operations

## Notes

- The project compiles successfully with zero errors on Windows PowerShell
- All source files include package declarations matching their directory structure
- The `bin/` folder contains compiled `.class` files (generated after compilation)
- The project is independent and requires only JDK installation

## Extension Ideas

To further enhance your learning:
1. Add unit tests using JUnit
2. Implement database connectivity (JDBC)
3. Create a GUI using Swing or JavaFX
4. Add networking capabilities with Sockets
5. Implement design patterns (Factory, Observer, Strategy)
6. Add configuration file handling (Properties, YAML)

## Resources

- [Oracle Java Documentation](https://docs.oracle.com/javase/)
- [Stream API Guide](https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html)
- [Concurrency Tutorial](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
- [Java Collections Framework](https://docs.oracle.com/javase/8/docs/technotes/guides/collections/overview.html)

---

**Project Status:** ✅ Complete and Fully Functional
**Last Updated:** December 2025
