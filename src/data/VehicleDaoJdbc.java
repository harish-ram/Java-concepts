package data;

import java.sql.*;
import java.util.*;
import models.*;

/**
 * A simple JDBC-based DAO for vehicles. Uses a standard JDBC API and
 * expects the driver to be available on the classpath (example: H2).
 */
public class VehicleDaoJdbc implements VehicleRepository {
    private final String url;
    private final String user;
    private final String password;

    public VehicleDaoJdbc(String url) {
        this(url, null, null);
    }

    public VehicleDaoJdbc(String url, String user, String password) {
        this.url = url; this.user = user; this.password = password;
    }

    private Connection getConnection() throws SQLException {
        if (user == null) return DriverManager.getConnection(url);
        return DriverManager.getConnection(url, user, password);
    }

    public void init() throws SQLException {
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
                s.execute("CREATE TABLE IF NOT EXISTS vehicles(" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "type VARCHAR(50), " +
                    "brand VARCHAR(100), " +
                    "model VARCHAR(100), " +
                    "manufacture_year INT, " +
                    "doors INT, " +
                    "fuel VARCHAR(50), " +
                    "sidecar BOOLEAN, " +
                    "category VARCHAR(100), " +
                    "payload DOUBLE, " +
                    "trailer BOOLEAN, " +
                    "cc INT)");
            // Seed sample data if table is empty (dev convenience)
            try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM vehicles")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    try {
                        // Insert a few sample vehicles
                        addVehicle(new models.Car("Toyota", "Camry", 2021, 4, "Petrol"));
                        addVehicle(new models.Bike("Honda", "CB500F", 2020, false, "Sports"));
                        addVehicle(new models.Truck("Ford", "F-150", 2019, 1500.0, false));
                        addVehicle(new models.Motorcycle("Yamaha", "R1", 2022, 1000, "Sports"));
                        System.out.println("Seeded sample vehicles into JDBC DB");
                    } catch (SQLException ignored) {
                        // ignore seeding errors - non-critical
                    }
                }
            }
        }
    }

    public void addVehicle(Vehicle v) throws SQLException {
        String sql = "INSERT INTO vehicles(id,type,brand,model,manufacture_year,doors,fuel,sidecar,category,payload,trailer,cc) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, v.getId());
            ps.setString(2, v.getClass().getSimpleName());
            ps.setString(3, v.getBrand());
            ps.setString(4, v.getModel());
            ps.setInt(5, v.getYear());
            if (v instanceof Car) { Car cobj = (Car) v; ps.setInt(6, cobj.getNumDoors()); ps.setString(7, cobj.getFuelType()); ps.setNull(8, Types.BOOLEAN); ps.setNull(9, Types.VARCHAR); ps.setNull(10, Types.DOUBLE); ps.setNull(11, Types.BOOLEAN); ps.setNull(12, Types.INTEGER); }
            else if (v instanceof Bike) { Bike b = (Bike) v; ps.setNull(6, Types.INTEGER); ps.setNull(7, Types.VARCHAR); ps.setBoolean(8, b.hasSidecar()); ps.setString(9, b.getType()); ps.setNull(10, Types.DOUBLE); ps.setNull(11, Types.BOOLEAN); ps.setNull(12, Types.INTEGER); }
            else if (v instanceof Truck) { Truck t = (Truck) v; ps.setNull(6, Types.INTEGER); ps.setNull(7, Types.VARCHAR); ps.setNull(8, Types.BOOLEAN); ps.setNull(9, Types.VARCHAR); ps.setDouble(10, t.getPayloadCapacityKg()); ps.setBoolean(11, t.hasTrailer()); ps.setNull(12, Types.INTEGER); }
            else if (v instanceof Motorcycle) { Motorcycle m = (Motorcycle) v; ps.setNull(6, Types.INTEGER); ps.setNull(7, Types.VARCHAR); ps.setNull(8, Types.BOOLEAN); ps.setNull(9, Types.VARCHAR); ps.setNull(10, Types.DOUBLE); ps.setNull(11, Types.BOOLEAN); ps.setInt(12, m.getEngineCc()); }
            else { // unknown type - set nulls
                ps.setNull(6, Types.INTEGER); ps.setNull(7, Types.VARCHAR); ps.setNull(8, Types.BOOLEAN); ps.setNull(9, Types.VARCHAR); ps.setNull(10, Types.DOUBLE); ps.setNull(11, Types.BOOLEAN); ps.setNull(12, Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }

    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(readVehicleFromResultSet(rs));
            }
        }
        System.out.println("[JDBC] getAllVehicles -> count=" + list.size());
        return list;
    }

    public Vehicle getVehicleById(String id) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return readVehicleFromResultSet(rs);
            }
        }
        System.out.println("[JDBC] getVehicleById(" + id + ") -> not found");
        return null;
    }

    public boolean updateVehicle(Vehicle v) throws SQLException {
        String sql = "UPDATE vehicles SET type=?,brand=?,model=?,manufacture_year=?,doors=?,fuel=?,sidecar=?,category=?,payload=?,trailer=?,cc=? WHERE id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, v.getClass().getSimpleName());
            ps.setString(2, v.getBrand());
            ps.setString(3, v.getModel());
            ps.setInt(4, v.getYear());
            if (v instanceof Car) { Car cobj = (Car) v; ps.setInt(5, cobj.getNumDoors()); ps.setString(6, cobj.getFuelType()); ps.setNull(7, Types.BOOLEAN); ps.setNull(8, Types.VARCHAR); ps.setNull(9, Types.DOUBLE); ps.setNull(10, Types.BOOLEAN); ps.setNull(11, Types.INTEGER); }
            else if (v instanceof Bike) { Bike b = (Bike) v; ps.setNull(5, Types.INTEGER); ps.setNull(6, Types.VARCHAR); ps.setBoolean(7, b.hasSidecar()); ps.setString(8, b.getType()); ps.setNull(9, Types.DOUBLE); ps.setNull(10, Types.BOOLEAN); ps.setNull(11, Types.INTEGER); }
            else if (v instanceof Truck) { Truck t = (Truck) v; ps.setNull(5, Types.INTEGER); ps.setNull(6, Types.VARCHAR); ps.setNull(7, Types.BOOLEAN); ps.setNull(8, Types.VARCHAR); ps.setDouble(9, t.getPayloadCapacityKg()); ps.setBoolean(10, t.hasTrailer()); ps.setNull(11, Types.INTEGER); }
            else if (v instanceof Motorcycle) { Motorcycle m = (Motorcycle) v; ps.setNull(5, Types.INTEGER); ps.setNull(6, Types.VARCHAR); ps.setNull(7, Types.BOOLEAN); ps.setNull(8, Types.VARCHAR); ps.setNull(9, Types.DOUBLE); ps.setNull(10, Types.BOOLEAN); ps.setInt(11, m.getEngineCc()); }
            else { ps.setNull(5, Types.INTEGER); ps.setNull(6, Types.VARCHAR); ps.setNull(7, Types.BOOLEAN); ps.setNull(8, Types.VARCHAR); ps.setNull(9, Types.DOUBLE); ps.setNull(10, Types.BOOLEAN); ps.setNull(11, Types.INTEGER); }
            ps.setString(12, v.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean removeVehicleById(String id) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Vehicle readVehicleFromResultSet(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String type = rs.getString("type");
        String brand = rs.getString("brand");
        String model = rs.getString("model");
        int year = rs.getInt("manufacture_year");
        switch (type.toLowerCase()) {
            case "car":
                int doors = rs.getInt("doors");
                String fuel = rs.getString("fuel");
                return new Car(id, brand, model, year, doors, fuel);
            case "bike":
                boolean side = rs.getBoolean("sidecar");
                String cat = rs.getString("category");
                return new Bike(id, brand, model, year, side, cat);
            case "truck":
                double payload = rs.getDouble("payload");
                boolean trailer = rs.getBoolean("trailer");
                return new Truck(id, brand, model, year, payload, trailer);
            case "motorcycle":
                int cc = rs.getInt("cc");
                String mcat = rs.getString("category");
                return new Motorcycle(id, brand, model, year, cc, mcat);
            default:
                return null;
        }
    }
}
