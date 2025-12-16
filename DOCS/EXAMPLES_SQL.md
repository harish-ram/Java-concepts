# SQL, Flyway, and JPA

This page documents the SQL schema and migration approach used by the project. Flyway runs migrations on Spring Boot startup to ensure the database schema is created and versioned.

## Flyway migration
- Migrations are located in `src/main/resources/db/migration` and are applied automatically at startup.
- Example migration (V1__create_vehicle_table.sql):

```sql
 1: CREATE TABLE IF NOT EXISTS vehicles (
 2:     id VARCHAR(255) PRIMARY KEY,
 3:     brand VARCHAR(255),
 4:     model VARCHAR(255),
 5:     manufacture_year INT,
 6:     vehicle_type VARCHAR(50),
 7:     num_doors INT,
 8:     fuel_type VARCHAR(100),
 9:     has_sidecar BOOLEAN,
10:     bike_type VARCHAR(100),
11:     payload_capacity_kg DOUBLE,
12:     has_trailer BOOLEAN,
13:     engine_cc INT,
14:     category VARCHAR(100)
15: );
```

## JPA Entities and Repository
- The `models` package contains JPA-annotated entities such as `Vehicle` and its subclass mappings. These map to the `vehicles` table in the migration.
- The `SpringDataVehicleRepository` (in `src/data`) uses an `EntityManager` and `@Transactional` methods to persist, update, and remove `Vehicle` entities.

## How it runs
- On application startup, Flyway validates and applies migrations (see logs: "Successfully applied 1 migration...").
- The repository uses JPA (Hibernate) to operate on `Vehicle` entities, and the service delegates to the repository for DB operations.

Files to review:
- `src/main/resources/db/migration/V1__create_vehicle_table.sql` — schema migration
- `src/models/Vehicle.java` and subclasses — entity mappings
- `src/data/SpringDataVehicleRepository.java` — JPA-backed repository implementation
