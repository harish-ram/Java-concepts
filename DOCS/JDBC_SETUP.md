JDBC DAO & H2 Setup
====================

This project includes a simple JDBC DAO implementation: `src/data/VehicleDaoJdbc.java` and a test runner `src/test/JdbcDaoTestRunner.java` that uses an H2 in-memory database.

Prerequisites
-------------
- JDK 11 installed
- Either Maven (optional) or the H2 JDBC driver jar on the classpath

Maven (recommended)
-------------------
If you have Maven installed, you can compile and run the test runner using the `exec` plugin:

```
mvn clean compile org.codehaus.mojo:exec-maven-plugin:3.1.0:exec -Dexec.mainClass=test.JdbcDaoTestRunner
```

This builds and runs the test runner using H2 from the dependencies declared in `pom.xml`.

Manual (no Maven)
-----------------
1. Download the H2 driver jar from https://repo1.maven.org/maven2/com/h2database/h2/2.1.214/h2-2.1.214.jar and place it under project `lib/` (create the folder first if needed).

2. Compile the project to the `bin` folder (without JUnit tests):

```powershell
javac -d bin src/models/*.java src/utilities/*.java src/data/*.java src/gui/*.java src/web/*.java src/concurrency/*.java src/test/BrandFilterTestRunner.java src/test/JdbcDaoTestRunner.java
```

3. Run the JDBC DAO test runner with the H2 jar on classpath:

```powershell
java -cp bin;lib/h2-2.1.214.jar test.JdbcDaoTestRunner
```

Expected result
---------------
- The test runner will create a schema and run basic CRUD operations.
- If the H2 driver isn't present, the runner prints an instruction message and exits.

Notes
-----
- The DAO uses standard JDBC only; you can adapt the connection URL to use other DB vendors if the driver is available on the classpath.
- If you'd like, I can wire the JDBC DAO into `VehicleDatabase` or into the web `Server` as an alternative persistence layer.
 - You can start the `Server` in JDBC mode (uses the `VehicleDaoJdbc` class) like this:

```powershell
java -cp "bin;lib/h2-2.1.214.jar" web.Server --jdbc --port=9000
```

 This will start the server using H2 in-memory DB and accept API calls to `http://localhost:9000/api/vehicles` for CRUD.
