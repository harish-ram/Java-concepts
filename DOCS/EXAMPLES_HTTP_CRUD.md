# HTTP CRUD (GET / POST / PUT / DELETE)

This page documents how to implement and use basic CRUD endpoints in this project using Spring MVC. It references the controller methods and shows examples of requests and expected responses.

## Endpoints
- GET /api/vehicles — returns list of vehicles (200 OK, JSON array)
- GET /api/vehicles/{id} — returns a single vehicle (200 OK) or 404 Not Found
- POST /api/vehicles/add — create a vehicle (201 Created with Location header and body {"id":"<uuid>"})
- PUT /api/vehicles/{id} — update a vehicle (200 OK) or 404 Not Found
- DELETE /api/vehicles/{id} — delete a vehicle (204 No Content) or 404 Not Found

Example implementation (see `src/web/SpringVehicleController.java`):

```java
 1: @GetMapping
 2: public List<Vehicle> listAll(@RequestParam(value = "brand", required = false) String brand,
 3:                              @RequestParam(value = "type", required = false) String type) throws Exception {
 4:     if ((brand != null && !brand.isEmpty()) || (type != null && !type.isEmpty())) {
 5:         return service.filterVehicles(brand, type);
 6:     }
 7:     return service.getAllVehicles();
 8: }
 9: 
10: @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
11: public ResponseEntity<Object> addVehicle(@RequestBody Map<String, Object> body) {
12:     // validates and delegates to service.addVehicle(v)
13: }
14: 
15: @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
16: public ResponseEntity<Object> updateVehicle(@PathVariable("id") String id, @RequestBody Map<String, Object> body) {
17:     // partial update via reflection/setters and service.updateVehicle(existing)
18: }
19: 
20: @DeleteMapping(path = "/{id}")
21: public ResponseEntity<Object> deleteVehicleById(@PathVariable("id") String id) {
22:     boolean ok = service.removeVehicleById(id);
23:     return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
24: }
```

## Sample request/response
- Create (POST /api/vehicles/add)
  - Request JSON: {"type":"car","brand":"Toyota","model":"Corolla","year":2020}
  - Response: 201 Created, Location: /api/vehicles/<id>, Body: {"id":"<id>"}

- Get (GET /api/vehicles)
  - Response: 200 OK, Body: [ {...}, {...} ]

- Update (PUT /api/vehicles/{id})
  - Request JSON: {"brand":"NewBrand"}
  - Response: 200 OK, Body: {"result":"updated"}

- Delete (DELETE /api/vehicles/{id})
  - Response: 204 No Content

## Files to review
- `src/web/SpringVehicleController.java` (controller)
- `src/services/VehicleService.java` (business layer)
- `src/test/java/test/VehicleControllerIT.java` (integration tests)

Notes:
- Controller returns consistent JSON error payloads using `src/web/GlobalExceptionHandler.java`.
- For richer validation, consider using DTOs + `@Valid` and `@ControllerAdvice` for binding errors.
