package runners;

/**
 * Placeholder runner to keep top-level test sources harmless when Maven adds
 * the top-level `src` directory as a source root. Real JUnit tests live under
 * `src/test/java/test`.
 */
public class VehicleDatabaseJUnitTest {
    public static void main(String[] args) {
        System.out.println("Top-level test placeholder (JUnit tests are in src/test/java/test)");
    }
}
