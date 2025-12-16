# Examples: JDBC Drivers, Connection & CRUD Operations

This document contains concise examples and references from the project that demonstrate JDBC use: loading drivers, creating connections, using PreparedStatement, performing CRUD, transactions, and proper resource handling.

> File pointers: `src/data/VehicleDaoJdbc.java`, `src/tools/DbMigrations.java`, `src/main/Main.java`.

---

## JDBC driver & connection ✅
File reference: `src/data/VehicleDaoJdbc.java`

```java
 1: // Ensure H2 driver is available on the classpath (Maven dependency)
 2: String url = "jdbc:h2:./vehicledb;DB_CLOSE_DELAY=-1";
 3: String user = "sa";
 4: String pass = "";
 5: 
 6: try (Connection conn = DriverManager.getConnection(url, user, pass)) {
 7:     // use connection
 8: } catch (SQLException ex) {
 9:     throw new RuntimeException("DB connection failed", ex);
10: }
```

Notes:
- Modern JDBC doesn't require Class.forName("org.h2.Driver") if the driver jar uses ServiceLoader metadata.
- Use `DriverManager.getConnection(...)` or a connection pool (HikariCP) for production.

---

## Create table (DDL) ✅
```java
1: String create = "CREATE TABLE IF NOT EXISTS vehicle (id VARCHAR PRIMARY KEY, brand VARCHAR, model VARCHAR, manufacture_year INT)";
2: try (Statement stmt = conn.createStatement()) {
3:     stmt.execute(create);
4: }
```

---

## CRUD examples (PreparedStatement & try-with-resources) ✅
File reference: `src/data/VehicleDaoJdbc.java`

```java
 1: // INSERT
 2: String insertSql = "INSERT INTO vehicle (id, brand, model, manufacture_year) VALUES (?, ?, ?, ?)";
 3: try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
 4:     ps.setString(1, v.getId());
 5:     ps.setString(2, v.getBrand());
 6:     ps.setString(3, v.getModel());
 7:     ps.setInt(4, v.getManufactureYear());
 8:     ps.executeUpdate();
 9: }
10: 
11: // SELECT
12: String selectSql = "SELECT id, brand, model, manufacture_year FROM vehicle WHERE id = ?";
13: try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
14:     ps.setString(1, id);
15:     try (ResultSet rs = ps.executeQuery()) {
16:         if (rs.next()) {
17:             // build Vehicle from rs
18:         }
19:     }
20: }
21: 
22: // UPDATE
23: String updateSql = "UPDATE vehicle SET brand = ?, model = ?, manufacture_year = ? WHERE id = ?";
24: try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
25:     ps.setString(1, v.getBrand());
26:     ps.setString(2, v.getModel());
27:     ps.setInt(3, v.getManufactureYear());
28:     ps.setString(4, v.getId());
29:     ps.executeUpdate();
30: }
31: 
32: // DELETE
33: String deleteSql = "DELETE FROM vehicle WHERE id = ?";
34: try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
35:     ps.setString(1, id);
36:     ps.executeUpdate();
37: }
```

---

## Transactions (commit/rollback) ✅
```java
 1: try {
 2:     conn.setAutoCommit(false);
 3:     // multiple operations
 4:     conn.commit();
 5: } catch (SQLException e) {
 6:     conn.rollback();
 7:     throw e;
 8: } finally {
 9:     conn.setAutoCommit(true);
10: }
```

---

## Error handling & best practices ✅
- Use `try-with-resources` for Connection/Statement/ResultSet to ensure closure.
- Catch and log `SQLException` and rethrow as application-specific exceptions where appropriate.
- Prefer `PreparedStatement` to avoid SQL injection and improve performance.
- Use connection pools for production workloads.

---

*Created: EXAMPLES_JDBC_CRUD.md*