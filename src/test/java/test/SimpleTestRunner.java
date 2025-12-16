package test;

public class SimpleTestRunner {
    public static void main(String[] args) throws Exception {
        System.out.println("Simple test runner: VehicleDatabase");
        test.VehicleDatabaseJUnitTest t = new test.VehicleDatabaseJUnitTest();
        t.testAddAndCount();
        t.testSaveLoadJson();
        System.out.println("Simple tests passed");
    }
}
