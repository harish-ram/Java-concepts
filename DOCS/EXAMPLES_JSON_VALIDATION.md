# JSON Handling & Validations

This page explains how JSON requests and validations are handled in the project and points to the files implementing them.

## JSON parsing & malformed payloads
- Controllers accept JSON via `@RequestBody` and Spring converts payloads into Java objects (`Map<String,Object>` in this project), or into DTOs when defined.
- Malformed JSON (invalid syntax) will raise a `HttpMessageNotReadableException` which is handled by `src/web/GlobalExceptionHandler.java` and returns 400 with JSON {"error":"Malformed JSON request"}.

## Validation approach used here
- Current approach: manual validation inside the controller (e.g., checking required fields `type`, `brand`, `model`, and `year`) and returning 400 with a JSON error map when missing or invalid.
- Example snippet from `SpringVehicleController`:

```java
1: String type = (String) body.getOrDefault("type", "");
2: if (type == null || type.trim().isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "Missing field: type"));
3: // ... validate brand, model, year
```

## Recommended improvement
- Use request DTOs and Java Bean Validation (e.g., `@NotNull`, `@Size`, `@Min`) plus `@Valid` on controller arguments. Add a binding error handler in `@ControllerAdvice` to return structured errors.

## Files to review
- `src/web/SpringVehicleController.java` — validation & JSON handling
- `src/web/GlobalExceptionHandler.java` — error handling for malformed JSON and other exceptions
- `src/test/java/test/VehicleControllerErrorIT.java` — tests for validation behaviors
