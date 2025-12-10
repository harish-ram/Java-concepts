# Java Learning Project

A hands-on, educational Java project that demonstrates core Java concepts through a small vehicle-management domain. Run the demos, inspect the code, and extend the project to deepen your knowledge.

---

## Quick Start (Windows PowerShell)
1. Open PowerShell and change to the project root:
```
cd C:\Users\ERS1399\Downloads\Full-Stack-Dev\JavaLearningProject
```
2. Compile the project into the `bin` folder:
```
javac -d bin src/models/*.java src/utilities/*.java src/concurrency/*.java src/data/*.java src/main/Main.java
```
3. Run the application:
```
java -cp bin main.Main
```

## Web Server & Web UI
You can start the built-in HTTP server (web UI and REST API) with:
```powershell
javac -d bin src/web/Server.java src/models/*.java src/utilities/*.java src/data/*.java src/concurrency/*.java
java -cp bin web.Server
```
Open http://localhost:8000 to view the web UI.

API endpoints:
- GET /api/vehicles - list vehicles (optional ?brand=...) 
- POST /api/vehicles/add - add vehicle (form body)
- POST /api/vehicles/update - update vehicle (form body with id)
- POST /api/vehicles/delete - delete vehicle (form body with id)
-- POST /api/vehicles/saveJson - save JSON
-- POST /api/vehicles/loadJson - load JSON
- GET /api/vehicles/{id} - get vehicle by id
- PUT /api/vehicles/{id} - update by id (best effort; use /update for compatibility)


---

## GUI (Swing)
The project includes a minimal Swing GUI that lists vehicles and allows adding new ones. Compile and run:
```
javac -d bin src/gui/*.java src/models/*.java src/utilities/*.java src/data/*.java
java -cp bin gui.VehicleGUI
```

UI improvements:
- Improved Swing layout with toolbar filters (brand & type), dynamic form fields per type, and Add/Edit dialogs with validation.
- Double-click table rows to edit; status updates visible in the UI footer.

	Web UI improvements:
	- Modal Add/Edit form with type-specific fields so users no longer type comma-separated extras.
	- Filter by brand and type in the UI, Save/Load JSON, and togglable ID visibility.
- Improved table layout and clear action buttons for an easier user experience.


---

## Simple Tests (no JUnit required)
There's a simple test runner that validates `VehicleDatabase` add/save/load behavior and basic stats. Compile & run:
```
javac -d bin src/test/SimpleTestRunner.java src/data/*.java src/models/*.java src/utilities/*.java
java -cp bin test.SimpleTestRunner
```

---

## Contributing & Suggestions
- Add unit tests for `VehicleDatabase` and core utilities using JUnit.
-- Add persistent storage (JSON or SQLite) to the interactive demo to retain vehicles between runs.

---

Happy learning! 

---

## Running JUnit tests (optional)
If you prefer using JUnit to run the tests added to `src/test/`, download the JUnit 4.13.2 and Hamcrest core jars and place them in a `libs/` folder at the project root, then run:
```powershell
javac -d bin -cp "libs/junit-4.13.2.jar;libs/hamcrest-core-1.3.jar" src/test/*.java src/models/*.java src/data/*.java src/utilities/*.java
java -cp "bin;libs/junit-4.13.2.jar;libs/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore VehicleDatabaseJUnitTest DataProcessorJUnitTest
```

