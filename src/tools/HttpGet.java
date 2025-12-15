package tools;

import java.io.*;
import java.net.*;

public class HttpGet {
    public static void main(String[] args) throws Exception {
        String url = args.length > 0 ? args[0] : "http://localhost:9000/api/vehicles";
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);
        int code = conn.getResponseCode();
        System.out.println("HTTP " + code);
        try (InputStream is = conn.getInputStream(); BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) System.out.println(line);
        }
    }
}
