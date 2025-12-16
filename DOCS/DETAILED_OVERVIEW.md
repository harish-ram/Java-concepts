# Java Learning Project — Detailed Overview

This repository is a compact, educational Vehicle Management System that demonstrates core Java concepts (OOP, collections, streams, concurrency), multiple persistence strategies, and practical tooling (Maven, Flyway, JPA/Hibernate, tests, a lightweight web API).

Key features
- Console demos and Swing GUI for interactive learning
- REST HTTP API for programmatic access ([web/Server])
- Persistence options: in-memory JSON store, JDBC (H2), and JPA (Hibernate) with Flyway migrations
- Tests (JUnit), Maven Wrapper, and a shaded runnable JAR

Quick facts
- Main entry: `src/main/Main.java`
- Web server: `src/web/Server.java` (endpoints under `/api/vehicles`)
- Data model: `src/models/*` (Car, Bike, Truck, Motorcycle, Vehicle)
- Repositories: `src/data/VehicleDatabaseRepository` (JSON/in-memory), `VehicleDaoJdbc` (JDBC/H2), `VehicleRepositoryJpa` (JPA/Hibernate)
- Migrations: SQL files under `src/main/resources/db/migration` (Flyway)

Running the app (recommended)

- Build (uses Maven wrapper):

```bash
./mvnw -B package
```

- Run server with in-memory JSON repository (fast, no extra deps):

```bash
java -jar target/java-learning-project-1.0.0-shaded.jar --server --port=8081 --no-demo
```

- Run server with JDBC (file-backed H2):

```bash
java -jar target/java-learning-project-1.0.0-shaded.jar --server --jdbc --jdbcUrl=jdbc:h2:./vehicledb;DB_CLOSE_DELAY=-1 --dbUser=sa --dbPass=
```

- Run server with JPA/Hibernate (Flyway migrations applied automatically):

```bash
java -jar target/java-learning-project-1.0.0-shaded.jar --server --jpa --jdbcUrl=jdbc:h2:./vehicledb;DB_CLOSE_DELAY=-1 --dbUser=sa --dbPass=
```

Notes about JPA and IDE builds
- The project includes lightweight `javax.persistence` stubs so `javac`/IDE builds work without a provider. To exercise full JPA behavior (Hibernate + Flyway migrations) build and run with Maven so the real provider and Flyway are on the classpath.

REST API (useful endpoints)
- `GET /api/vehicles` — list all (supports `?brand=` and `?type=` filters)
- `GET /api/vehicles/{id}` — get vehicle by id
- `POST /api/vehicles/add` — add vehicle (form/body params)
- `POST /api/vehicles/update` — update vehicle
- `POST /api/vehicles/delete` — delete vehicle
- `POST /api/vehicles/loadJson` — reload `vehicles.json` into in-memory store

Project flow summary

- Startup (`main.Main`) parses CLI flags and selects repository implementation.
- If `--jpa` is selected, Flyway migrations run, then Hibernate's `EntityManagerFactory` is created.
- `VehicleService` implements business logic and delegates persistence to the selected repository.
- `web.Server` exposes REST endpoints over a small, embeddable HTTP server.

Testing & CI
- Unit and integration tests live in `src/test/java` and run with Surefire/JUnit.
- A GitHub Actions workflow and Maven Wrapper are included for reproducible CI runs.

If you want
- I can add an automated integration test exercising the HTTP endpoints and DB flows.
- Or I can create a short README snippet with exact commands to run the server, run tests, and switch between JSON/JDBC/JPA modes.

Files to inspect for specifics
- `src/main/Main.java` — CLI flags and demo wiring
- `src/web/Server.java` — HTTP handlers
- `src/services/VehicleService.java` — business logic and validation
- `src/data/VehicleDaoJdbc.java` — JDBC implementation
- `src/data/VehicleRepositoryJpa.java` — JPA implementation + Flyway migration runner
- `src/main/resources/db/migration/` — Flyway SQL migrations

---

This overview reflects the current repository state (JPA + Flyway integrated, tests and Maven wrapper added). If you'd like the doc more concise or targeted (e.g., only API or only running instructions), tell me which view to produce and I'll rewrite it accordingly.
