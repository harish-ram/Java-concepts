package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import data.VehicleDatabaseRepository;
import data.VehicleRepository;
import data.VehicleDaoJdbc;
import services.VehicleService;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import models.*;

public class Server {
    private final VehicleRepository repo;
    private final VehicleService service;
    private final HttpServer httpServer;
    private final CountDownLatch stopLatch = new CountDownLatch(1);
    private final String adminToken;

    public Server(int port) throws IOException {
        this(port, new VehicleDatabaseRepository(), null);
    }

    public Server(int port, VehicleRepository repo, String adminToken) throws IOException {
        this.repo = repo;
        this.adminToken = adminToken;
        this.service = new VehicleService(repo);
        // If repository wraps the in-memory DB, attempt to load JSON
        try {
            if (repo instanceof VehicleDatabaseRepository) {
                // load persisted file
                ((VehicleDatabaseRepository) repo).loadFromJson("vehicles.json");
            } else {
                // if JDBC, attempt initialization
                repo.init();
            }
        } catch (Exception ignored) {}
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        httpServer.createContext("/api/vehicles", this::handleVehicles);
        httpServer.createContext("/api/vehicles/add", this::handleAdd);
        httpServer.createContext("/api/vehicles/delete", this::handleDelete);
        httpServer.createContext("/api/vehicles/update", this::handleUpdate);
        httpServer.createContext("/api/vehicles/", this::handleVehicles); // handle /api/vehicles/{id}
        // CSV save/load deprecated; keep JSON endpoints only
        httpServer.createContext("/api/vehicles/saveJson", this::handleSaveJson);
        httpServer.createContext("/api/vehicles/loadJson", this::handleLoadJson);
        // admin endpoints
        httpServer.createContext("/api/admin/shutdown", this::handleAdminShutdown);
        httpServer.createContext("/", this::handleStatic);
        httpServer.setExecutor(null);
    }

    public void stop() {
        httpServer.stop(1);
        System.out.println("Server stopped at http://localhost:" + httpServer.getAddress().getPort());
        stopLatch.countDown();
    }

    public void start() {
        httpServer.start();
        System.out.println("Server started at http://localhost:" + httpServer.getAddress().getPort());
    }

    public void waitForStop() throws InterruptedException {
        stopLatch.await();
    }

    private void handleVehicles(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.startsWith("/api/vehicles/") && path.length() > "/api/vehicles/".length()) {
            String id = path.substring("/api/vehicles/".length());
            // Reserved names like /api/vehicles/add or /api/vehicles/delete must not be treated as id
            if (id.equals("add") || id.equals("delete") || id.equals("update") || id.equals("saveJson") || id.equals("loadJson")) {
                id = null; // treat as non-id, let specific endpoints handle
            }
            if (id != null) {
            String method = exchange.getRequestMethod();
            if (method.equalsIgnoreCase("GET")) {
                try {
                        Vehicle v = service.getVehicleById(id);
                    if (v == null) { exchange.sendResponseHeaders(404, -1); return; }
                    sendJson(exchange, vehicleToJson(v));
                } catch (Exception ex) { sendJson(exchange, "{\"ok\":false, \"error\":\"" + ex.getMessage() + "\"}"); }
                return;
            } else if (method.equalsIgnoreCase("DELETE")) {
                try {
                    boolean removed = service.removeVehicleById(id);
                    sendJson(exchange, "{\"ok\": " + removed + "}");
                } catch (Exception ex) { sendJson(exchange, "{\"ok\":false, \"error\":\"" + ex.getMessage() + "\"}"); }
                return;
            } else if (method.equalsIgnoreCase("PUT") || method.equalsIgnoreCase("POST")) {
                try {
                    Map<String, String> params = parseQuery(exchange);
                    String type = params.getOrDefault("type", "").toLowerCase();
                    Vehicle pv = decodeVehicleFromParams(type, params, id);
                    if (pv == null) { sendJson(exchange, "{\"ok\":false, \"error\":\"unknown type\"}"); return; }
                    boolean ok = service.updateVehicle(pv);
                    sendJson(exchange, "{\"ok\": " + ok + "}");
                } catch (Exception ex) { sendJson(exchange, "{\"ok\":false, \"error\":\"" + ex.getMessage() + "\"}"); }
                return;
            } else {
                exchange.sendResponseHeaders(405, -1); return;
            }
            }
        }
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) { exchange.sendResponseHeaders(405, -1); return; }
        Map<String,String> query = parseQuery(exchange);
        String brandFilter = query.get("brand");
        String typeFilter = query.get("type");
        System.out.println("GET /api/vehicles query: brand=" + brandFilter + " type=" + typeFilter);
        List<Vehicle> list;
        try { list = service.getAllVehicles(); } catch (Exception ex) { sendJson(exchange, "{\"ok\":false, \"error\":\"" + ex.getMessage() + "\"}"); return; }
        if (brandFilter != null && !brandFilter.isEmpty()) {
            final String q = brandFilter.trim().toLowerCase();
            list = list.stream().filter(v -> v.getBrand() != null && v.getBrand().toLowerCase().contains(q)).collect(Collectors.toList());
        }
        if (typeFilter != null && !typeFilter.isEmpty()) {
            list = list.stream().filter(v -> v.getClass().getSimpleName().equalsIgnoreCase(typeFilter)).collect(Collectors.toList());
        }
        String body = toJson(list);
        sendJson(exchange, body);
    }

    private void handleAdd(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        Map<String,String> params = parseQuery(exchange);
        String type = params.getOrDefault("type", "").toLowerCase();
        try {
            Vehicle v = decodeVehicleFromParams(type, params);
            if (v != null) {
                service.addVehicle(v);
                sendJson(exchange, "{\"ok\":true}");
                return;
            }
            sendJson(exchange, "{\"ok\":false, \"error\":\"unknown type\"}");
        } catch (Exception e) {
            sendJson(exchange, "{\"ok\":false, \"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.startsWith("/api/vehicles/") && path.length() > "/api/vehicles/".length() && !path.equals("/api/vehicles/delete")) {
            // Delete by path id
            if (!exchange.getRequestMethod().equalsIgnoreCase("DELETE") && !exchange.getRequestMethod().equalsIgnoreCase("POST")) { exchange.sendResponseHeaders(405, -1); return; }
            String id = path.substring("/api/vehicles/".length());
            // Debugging removed: perform delete
            try { boolean removed = service.removeVehicleById(id); sendJson(exchange, "{\"ok\": " + removed + "}"); } catch (Exception ex) { sendJson(exchange, "{\"ok\":false, \"error\":\"" + ex.getMessage() + "\"}"); }
            return;
        }
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { exchange.sendResponseHeaders(405, -1); return; }
        Map<String,String> params = parseQuery(exchange);
        String id = params.get("id");
        // param id delete
        if (id == null) { sendJson(exchange, "{\"ok\":false, \"error\":\"id missing\"}"); return; }
                try { boolean removed = service.removeVehicleById(id); sendJson(exchange, "{\"ok\": " + removed + "}"); } catch (Exception ex) { sendJson(exchange, "{\"ok\":false, \"error\":\"" + ex.getMessage() + "\"}"); }
    }

    private void handleUpdate(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String idFromPath = null;
        if (path.startsWith("/api/vehicles/") && path.length() > "/api/vehicles/".length()) {
            idFromPath = path.substring("/api/vehicles/".length());
            if (idFromPath.equals("add") || idFromPath.equals("delete") || idFromPath.equals("update") || idFromPath.equals("saveJson") || idFromPath.equals("loadJson")) {
                idFromPath = null;
            }
        }
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST") && !exchange.getRequestMethod().equalsIgnoreCase("PUT")) { exchange.sendResponseHeaders(405, -1); return; }
        Map<String,String> params = parseQuery(exchange);
        String id = params.get("id");
        if (id == null) id = idFromPath;
        String type = params.getOrDefault("type", "").toLowerCase();
        if (id == null) { sendJson(exchange, "{\"ok\":false, \"error\":\"id missing\"}"); return; }
        try {
            Vehicle v = decodeVehicleFromParams(type, params);
            if (v != null) {
                // preserve id: construct v with id
                // decodeVehicleFromParams to create with id
                Vehicle pv = decodeVehicleFromParams(type, params, id);
                if (pv != null) {
                    boolean ok = service.updateVehicle(pv);
                    sendJson(exchange, "{\"ok\": " + ok + "}");
                    return;
                }
                // fallback: for replace with different id, attempt update; or remove+add
                try { service.removeVehicleById(id); service.addVehicle(v); } catch (Exception ex) { sendJson(exchange, "{\"ok\":false, \"error\":\"" + ex.getMessage() + "\"}"); return; }
                sendJson(exchange, "{\"ok\":true}");
                return;
            }
            sendJson(exchange, "{\"ok\":false, \"error\":\"unknown type\"}");
        } catch (Exception e) { sendJson(exchange, "{\"ok\":false, \"error\":\"" + e.getMessage() + "\"}"); }
    }

    // CSV endpoints removed; use /api/vehicles/saveJson instead

    private void handleSaveJson(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { exchange.sendResponseHeaders(405, -1); return; }
        try { service.saveToJson("vehicles.json"); sendJson(exchange, "{\"ok\":true}"); return; } catch (Exception ex) { sendJson(exchange, "{\"ok\":false, \"error\":\"" + ex.getMessage() + "\"}"); return; }
    }

    // CSV endpoints removed; use /api/vehicles/loadJson instead

    private void handleLoadJson(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { exchange.sendResponseHeaders(405, -1); return; }
        try { service.loadFromJson("vehicles.json"); sendJson(exchange, "{\"ok\":true}"); return; } catch (Exception ex) { sendJson(exchange, "{\"ok\":false, \"error\":\"" + ex.getMessage() + "\"}"); return; }
    }

    private Vehicle decodeVehicleFromParams(String type, Map<String,String> params) {
        return decodeVehicleFromParams(type, params, null);
    }

    private Vehicle decodeVehicleFromParams(String type, Map<String,String> params, String id) {
        String brand = params.getOrDefault("brand", "");
        String model = params.getOrDefault("model", "");
        int year = Integer.parseInt(params.getOrDefault("year", "0"));
        switch (type) {
            case "car":
                int doors = Integer.parseInt(params.getOrDefault("doors", "4"));
                String fuel = params.getOrDefault("fuel", "Petrol");
                if (id == null) return new Car(brand, model, year, doors, fuel);
                else return new Car(id, brand, model, year, doors, fuel);
            case "bike":
                boolean side = Boolean.parseBoolean(params.getOrDefault("sidecar", "false"));
                String cat = params.getOrDefault("category", "Cruiser");
                if (id == null) return new Bike(brand, model, year, side, cat);
                else return new Bike(id, brand, model, year, side, cat);
            case "truck":
                double payload = Double.parseDouble(params.getOrDefault("payload", "0"));
                boolean trailer = Boolean.parseBoolean(params.getOrDefault("trailer", "false"));
                if (id == null) return new Truck(brand, model, year, payload, trailer);
                else return new Truck(id, brand, model, year, payload, trailer);
            case "motorcycle":
                int cc = Integer.parseInt(params.getOrDefault("cc", "500"));
                String mcat = params.getOrDefault("category", "Sports");
                if (id == null) return new Motorcycle(brand, model, year, cc, mcat);
                else return new Motorcycle(id, brand, model, year, cc, mcat);
            default:
                return null;
        }
    }

    private Map<String,String> parseQuery(HttpExchange exchange) throws IOException {
        Map<String,String> map = new HashMap<>();
        String method = exchange.getRequestMethod();
        if (method.equalsIgnoreCase("GET")) {
            URI uri = exchange.getRequestURI();
            String raw = uri.getRawQuery();
            if (raw != null) map.putAll(parse(raw));
        } else {
            // read body as form data or query string
            try (InputStream is = exchange.getRequestBody(); BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String body = br.lines().collect(Collectors.joining("\n"));
                map.putAll(parse(body));
            }
        }
        return map;
    }

    private Map<String,String> parse(String raw) {
        Map<String,String> map = new HashMap<>();
        if (raw == null || raw.isEmpty()) return map;
        String[] parts = raw.split("&");
        for (String p : parts) {
            String[] kv = p.split("=", 2);
            try {
                String k = URLDecoder.decode(kv[0], StandardCharsets.UTF_8.name());
                String v = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name()) : "";
                map.put(k, v);
            } catch (UnsupportedEncodingException e) { /* ignore */ }
        }
        return map;
    }

    private String toJson(List<Vehicle> vehicles) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (Vehicle v : vehicles) {
            if (!first) sb.append(",");
            first = false;
            sb.append(vehicleToJson(v));
        }
        sb.append("]");
        return sb.toString();
    }

    private String vehicleToJson(Vehicle v) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":\"").append(v.getId()).append("\"");
        sb.append(",\"type\":\"").append(v.getClass().getSimpleName()).append("\"");
        sb.append(",\"brand\":\"").append(escape(v.getBrand())).append("\"");
        sb.append(",\"model\":\"").append(escape(v.getModel())).append("\"");
        sb.append(",\"year\":").append(v.getYear());
        if (v instanceof Car) {
            Car c = (Car) v;
            sb.append(",\"doors\":").append(c.getNumDoors());
            sb.append(",\"fuel\":\"").append(escape(c.getFuelType())).append("\"");
        } else if (v instanceof Bike) {
            Bike b = (Bike) v;
            sb.append(",\"sidecar\":").append(b.hasSidecar());
            sb.append(",\"category\":\"").append(escape(b.getType())).append("\"");
        } else if (v instanceof Truck) {
            Truck t = (Truck) v;
            sb.append(",\"payload\":").append(t.getPayloadCapacityKg());
            sb.append(",\"trailer\":").append(t.hasTrailer());
        } else if (v instanceof Motorcycle) {
            Motorcycle m = (Motorcycle) v;
            sb.append(",\"cc\":").append(m.getEngineCc());
            sb.append(",\"category\":\"").append(escape(m.getCategory())).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }

    private void sendJson(HttpExchange exchange, String body) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(200, body.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(body.getBytes(StandardCharsets.UTF_8)); }
    }

    private void handleStatic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) path = "/index.html";
        Path filePath = Paths.get("web/" + path).normalize();
        if (!filePath.startsWith(Paths.get("web/"))) { exchange.sendResponseHeaders(403, -1); return; }
        if (!Files.exists(filePath)) { exchange.sendResponseHeaders(404, -1); return; }
        String type = Files.probeContentType(filePath);
        exchange.getResponseHeaders().add("Content-Type", type == null ? "application/octet-stream" : type);
        byte[] bytes = Files.readAllBytes(filePath);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private void handleAdminShutdown(HttpExchange exchange) throws IOException {
        // Only allow POST
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) { exchange.sendResponseHeaders(405, -1); return; }
        // Check remote address is loopback or admin token is provided
        boolean allowed = false;
        java.net.InetAddress remote = exchange.getRemoteAddress().getAddress();
        if (remote.isLoopbackAddress() || remote.isAnyLocalAddress()) { allowed = true; }
        if (!allowed && adminToken != null && !adminToken.isEmpty()) {
            List<String> tokens = exchange.getRequestHeaders().getOrDefault("X-Admin-Token", java.util.Collections.emptyList());
            if (!tokens.isEmpty() && adminToken.equals(tokens.get(0))) allowed = true;
        }
        if (!allowed) { exchange.sendResponseHeaders(403, -1); return; }
        // send response and stop server after a short delay to allow response to flush
        String body = "{\"ok\":true, \"msg\":\"shutting down\"}";
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, body.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(body.getBytes(StandardCharsets.UTF_8)); }
        new Thread(() -> { try { Thread.sleep(200); stop(); } catch (Exception ignored) {} }).start();
    }

    public static void main(String[] args) throws Exception {
        int port = 8000;
        boolean useJdbc = false;
        String jdbcUrl = "jdbc:h2:mem:vehicledb;DB_CLOSE_DELAY=-1";
        String adminToken = System.getenv("ADMIN_TOKEN");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASS");
        if (args.length > 0) {
            for (String a : args) {
                if (a.equalsIgnoreCase("--jdbc")) useJdbc = true;
                else if (a.startsWith("--jdbcUrl=")) jdbcUrl = a.substring("--jdbcUrl=".length());
                else if (a.startsWith("--port=")) port = Integer.parseInt(a.substring("--port=".length()));
                else if (a.startsWith("--admin-token=")) adminToken = a.substring("--admin-token=".length());
                else if (a.startsWith("--dbUser=")) dbUser = a.substring("--dbUser=".length());
                else if (a.startsWith("--dbPass=")) dbPass = a.substring("--dbPass=".length());
            }
        }
        Server srv;
        if (useJdbc) {
            System.out.println("Starting server with JDBC repository, url=" + jdbcUrl + " port=" + port);
            VehicleDaoJdbc dao = new VehicleDaoJdbc(jdbcUrl, dbUser, dbPass);
            dao.init();
            srv = new Server(port, dao, adminToken);
        } else {
            System.out.println("Starting server with in-memory JSON repository on port=" + port);
            srv = new Server(port, new data.VehicleDatabaseRepository(), adminToken); // uses in-memory db
        }
        srv.start();
    }
}
