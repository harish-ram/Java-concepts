# Controllers, Services, and Dependency Injection

This document highlights how Controllers, Services, and Dependency Injection (DI) are implemented in this project. It includes short code snippets and references to the exact files where the patterns are used.

## Controllers
Controllers are the entry point for HTTP requests in a Spring MVC application. They decode HTTP requests, validate inputs, and delegate business logic to service classes.

Example: `src/web/SpringVehicleController.java`

```java
 1: @RestController
 2: @RequestMapping("/api/vehicles")
 3: public class SpringVehicleController {
 4:     private final VehicleService service;
 5: 
 6:     public SpringVehicleController(VehicleService service) {
 7:         this.service = service;
 8:     }
 9: 
10:     @GetMapping
11:     public List<Vehicle> listAll(...) throws Exception {
12:         return service.getAllVehicles();
13:     }
14: 
15:     @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
16:     public ResponseEntity<Object> addVehicle(@RequestBody Map<String, Object> body) {
17:         // validate body fields, build `Vehicle` and call `service.addVehicle(v)`
18:     }
19: }
```

Where implemented in this repo:
- `src/web/SpringVehicleController.java` — full CRUD endpoints, validation, and error responses
- `src/web/GlobalExceptionHandler.java` — centralized JSON error responses

## Services
Services contain business logic and coordinate repositories and other components. They are annotated with `@Service` and are injected into controllers.

Example: `src/services/VehicleService.java`

```java
 1: @Service
 2: public class VehicleService {
 3:     private final VehicleRepository repo;
 4: 
 5:     @Autowired
 6:     public VehicleService(VehicleRepository repo) {
 7:         this.repo = repo;
 8:         try { this.repo.init(); } catch (Exception ignored) {}
 9:     }
10: 
11:     public void addVehicle(Vehicle v) throws Exception { repo.addVehicle(v); }
12:     public List<Vehicle> getAllVehicles() throws Exception { return repo.getAllVehicles(); }
13: }
```

Where implemented in this repo:
- `src/services/VehicleService.java` — service methods used by the controller and tests

## Dependency Injection (DI)
DI is how Spring wires components together. We use `@Service`, `@Repository`, and constructor injection to keep components decoupled and testable.

Example repo implementation:

```java
1: @Repository
2: public class SpringDataVehicleRepository implements VehicleRepository {
3:     @PersistenceContext
4:     private EntityManager em;
5: 
6:     @Transactional
7:     public void addVehicle(Vehicle v) { em.persist(v); }
8: }
```

Files:
- `src/data/SpringDataVehicleRepository.java` — JPA-backed repository used by `VehicleService`
- `src/web/GlobalExceptionHandler.java` — `@ControllerAdvice` centralizes error handling

## How it fits together
- `SpringVehicleController` receives HTTP requests and delegates to `VehicleService`.
- `VehicleService` implements business operations and uses `VehicleRepository` (the `SpringDataVehicleRepository` implementation) to interact with the DB.
- Spring's DI creates and wires these objects at runtime, so controllers don't `new` services or repositories directly.

## Notes & Tips
- Prefer constructor injection (used in `SpringVehicleController`) because it makes components immutable and easier to unit test.
- Use `@ControllerAdvice` to handle errors in a consistent JSON format for API clients.
- Keep controllers thin — concentrate validation and business rules in services.

---

Generated from project files; create a PPT for presentations by running the generator script in `tools/`.
