# JPA, Hibernate, Repositories & Migrations

This doc explains how JPA, Hibernate, repositories and Flyway migrations are used in this project and how to connect Spring Boot to an external SQL database (PostgreSQL example).

## Overview
- Flyway is used for schema migrations (SQL files in `src/main/resources/db/migration`).
- Entities are JPA-annotated Java classes in `src/models` (single-table inheritance for `Vehicle` + subclasses).
- Repository implementation used in Spring: `src/data/SpringDataVehicleRepository.java` (uses `EntityManager`).
- `src/services/VehicleService.java` is the service layer that delegates to the repository.

## Flyway migration
- Migration files live in `src/main/resources/db/migration` and are applied automatically at startup.
- Example: `V1__create_vehicle_table.sql` creates the `vehicles` table.
- Flyway logs show successful application on startup: "Successfully applied 1 migration to schema \"PUBLIC\""

## JPA / Hibernate
- Entities map Java objects to database rows (see `src/models/Vehicle.java` and subclasses).
- Hibernate is the JPA provider configured by Spring Boot. We set `spring.jpa.hibernate.ddl-auto=none` to let Flyway manage schema changes.
- SQL logging is enabled in `application.properties` via `spring.jpa.show-sql=true` and `logging.level.org.hibernate.SQL=DEBUG`.

## Repository
- `SpringDataVehicleRepository` uses `@Repository`, `@PersistenceContext` (EntityManager), and `@Transactional` for write operations.
- This decouples persistence from service and controller layers and makes the code testable.

## Connect Spring Boot to PostgreSQL (example)
Create an `application-postgres.properties` file (or set env vars) and run the app with the `postgres` profile.

Example `src/main/resources/application-postgres.properties`:

```
1: spring.datasource.url=jdbc:postgresql://localhost:5432/vehicledb
2: spring.datasource.username=veh_user
3: spring.datasource.password=veh_pass
4: spring.datasource.driver-class-name=org.postgresql.Driver
5: 
6: spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
7: spring.jpa.hibernate.ddl-auto=none
8: spring.flyway.enabled=true
```

Run a local Postgres quickly (Docker example):

```bash
1: # Start a Postgres container (docker must be installed)
2:  docker run --name pg-veh -e POSTGRES_PASSWORD=veh_pass -e POSTGRES_USER=veh_user -e POSTGRES_DB=vehicledb -p 5432:5432 -d postgres:15
```

Start the app using the `postgres` profile:

```bash
1: # With Maven
2: ./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
3: # Or with the jar
4: java -Dspring.profiles.active=postgres -jar target/java-learning-project-1.0.0.jar
```

Alternatively, you can pass connection properties via environment variables (useful in CI or containers):
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD

## Notes & Troubleshooting
- Ensure Postgres is reachable and credentials are correct.
- Flyway will apply migrations on app startup — check logs for migration output.
- If you see `Port 8081 already in use`, stop the process using it or change `server.port` in `application.properties`.

## Files to review
- `src/main/resources/db/migration/V1__create_vehicle_table.sql`
- `src/models/Vehicle.java` and subclasses
- `src/data/SpringDataVehicleRepository.java`
- `src/web/SpringVehicleController.java` (how controllers use service/repository)
- `src/services/VehicleService.java`

---
Generated based on the projects' code and migration files.
