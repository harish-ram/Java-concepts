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
Dev helper: run all components with a single script
-----------------------------------------------
There's a small PowerShell helper script `run-dev.ps1` included to start the H2 console, server (file-based H2 DB), and GUI with default dev settings.

To use it, run:
```powershell
cd C:\Users\ERS1399\Downloads\Full-Stack-Dev\JavaLearningProject
.\run-dev.ps1
```

This will start the H2 console on port 8082, the server on port 9000 (with `jdbc:h2:./vehicledb`), and the Swing GUI.

You can also customize database credentials and behavior by running `main.Main` with CLI flags. Example:
```powershell
java -cp "bin;lib/h2-2.1.214.jar" main.Main --jdbc --port=9000 --dbUser=sa --dbPass="" --no-demo --start-h2
```
Notes:
- `--jdbc` enables JDBC mode (defaults to `jdbc:h2:./vehicledb;DB_CLOSE_DELAY=-1`).
- `--dbUser` and `--dbPass` override JDBC credentials (default is `sa` with empty password).
 - If credentials are not supplied via CLI, `main.Main` will fallback to environment variables `DB_USER` and `DB_PASS`.
 - `--create-db` will create database schema (if missing) on startup when running with `--jdbc`.
 - `--admin-token` or environment variable `ADMIN_TOKEN` will set an admin token to restrict the shutdown endpoint; the admin shutdown endpoint is `/api/admin/shutdown` and accepts POST from localhost or requests with the header `X-Admin-Token: <token>`.
- `--no-demo` skips the interactive demos and keeps server running.
- `--start-h2` tries to start the H2 console programmatically (dev-only).



Brand filter demo (ad-hoc runner):
```
javac -d bin src/test/BrandFilterTestRunner.java src/data/*.java src/models/*.java
java -cp bin test.BrandFilterTestRunner
```

JDBC DAO demo (requires H2 in `lib/h2-2.1.214.jar`):
```
javac -d bin -cp "lib/h2-2.1.214.jar" src/test/JdbcDaoTestRunner.java src/data/*.java src/models/*.java
java -cp "bin;lib/h2-2.1.214.jar" test.JdbcDaoTestRunner
```

JPA + Flyway (recommended when using Maven):

- The project supports a JPA-backed repository via Hibernate. Flyway migrations are stored under `src/main/resources/db/migration` and are applied automatically when the JPA repository initializes.
- Build and run with Maven to ensure dependencies are resolved:

```
mvn package
java -jar target/java-learning-project-1.0.0.jar --server --jpa --jdbcUrl=jdbc:h2:./vehicledb;DB_CLOSE_DELAY=-1
```

This will run Flyway migrations and start the server using the H2 database file `vehicledb` in the project root.

Note: To make the project compile with plain `javac` (IDE-only workflows) minimal `javax.persistence` stubs are included under `src/javax/persistence`. These are only compile-time placeholders so demos and non-JPA flows run without Maven. For full JPA/Hibernate functionality you must build and run with Maven so the real provider and Flyway are present on the classpath.

To create the Maven Wrapper locally (recommended for reproducible builds), run:

```powershell
scripts\generate-maven-wrapper.ps1
```

Or run the following directly if you have Maven installed:

```powershell
mvn -N io.takari:maven:wrapper
```

After generating the wrapper, commit `mvnw`, `mvnw.cmd` and the `.mvn/wrapper/` directory to the repository. Placeholder wrapper scripts `mvnw` and `mvnw.cmd` are included; if you run them and the wrapper jar is missing, the scripts will instruct you how to generate the wrapper.

CI note: the GitHub Actions workflow prefers `./mvnw` if present, otherwise it falls back to `mvn`.
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

