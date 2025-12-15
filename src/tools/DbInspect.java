package tools;

import data.VehicleDaoJdbc;

public class DbInspect {
    public static void main(String[] args) throws Exception {
        String url = args.length > 0 ? args[0] : "jdbc:h2:./vehicledb";
        String user = args.length > 1 ? args[1] : "sa";
        String pass = args.length > 2 ? args[2] : "";
        VehicleDaoJdbc dao = new VehicleDaoJdbc(url, user, pass);
        dao.init();
        System.out.println("Vehicle count: " + dao.getAllVehicles().size());
    }
}
