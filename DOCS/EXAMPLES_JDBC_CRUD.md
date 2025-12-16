# Examples: JDBC Drivers, Connection & CRUD Operations

This document contains concise examples and references from the project that demonstrate JDBC use: loading drivers, creating connections, using PreparedStatement, performing CRUD, transactions, and proper resource handling.

> File pointers: `src/data/VehicleDaoJdbc.java`, `src/tools/DbMigrations.java`, `src/main/Main.java`.

---

## JDBC driver & connection ✅
File reference: `src/data/VehicleDaoJdbc.java`

```java
// Ensure H2 driver is available on the classpath (Maven dependency)
String url = "jdbc:h2:./vehicledb;DB_CLOSE_DELAY=-1";
String user = "sa";
String pass = "";

try (Connection conn = DriverManager.getConnection(url, user, pass)) {
    // use connection
} catch (SQLException ex) {
    throw new RuntimeException("DB connection failed", ex);
}
```

Notes:
- Modern JDBC doesn't require Class.forName("org.h2.Driver") if the driver jar uses ServiceLoader metadata.
- Use `DriverManager.getConnection(...)` or a connection pool (HikariCP) for production.

---

## Create table (DDL) ✅
```java
String create = "CREATE TABLE IF NOT EXISTS vehicle (id VARCHAR PRIMARY KEY, brand VARCHAR, model VARCHAR, manufacture_year INT)";
try (Statement stmt = conn.createStatement()) {
    stmt.execute(create);
}
```

---

## CRUD examples (PreparedStatement & try-with-resources) ✅
File reference: `src/data/VehicleDaoJdbc.java`

```java
// INSERT
String insertSql = "INSERT INTO vehicle (id, brand, model, manufacture_year) VALUES (?, ?, ?, ?)";
try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
    ps.setString(1, v.getId());
    ps.setString(2, v.getBrand());
    ps.setString(3, v.getModel());
    ps.setInt(4, v.getManufactureYear());
    ps.executeUpdate();
}

// SELECT
String selectSql = "SELECT id, brand, model, manufacture_year FROM vehicle WHERE id = ?";
try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
    ps.setString(1, id);
    try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            // build Vehicle from rs
        }
    }
}

// UPDATE
String updateSql = "UPDATE vehicle SET brand = ?, model = ?, manufacture_year = ? WHERE id = ?";
try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
    ps.setString(1, v.getBrand());
    ps.setString(2, v.getModel());
    ps.setInt(3, v.getManufactureYear());
    ps.setString(4, v.getId());
    ps.executeUpdate();
}

// DELETE
String deleteSql = "DELETE FROM vehicle WHERE id = ?";
try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
    ps.setString(1, id);
    ps.executeUpdate();
}
```

---

## Transactions (commit/rollback) ✅
```java
try {
    conn.setAutoCommit(false);
    // multiple operations
    conn.commit();
} catch (SQLException e) {
    conn.rollback();
    throw e;
} finally {
    conn.setAutoCommit(true);
}
```

---

## Error handling & best practices ✅
- Use `try-with-resources` for Connection/Statement/ResultSet to ensure closure.
- Catch and log `SQLException` and rethrow as application-specific exceptions where appropriate.
- Prefer `PreparedStatement` to avoid SQL injection and improve performance.
- Use connection pools for production workloads.

---

*Created: EXAMPLES_JDBC_CRUD.md*