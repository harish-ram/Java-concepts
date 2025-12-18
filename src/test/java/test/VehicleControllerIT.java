package test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(classes = main.SpringBootApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class VehicleControllerIT {

    @Autowired
    private TestRestTemplate rest;

    @Test
    public void postThenGet_vehicleIsPersisted() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("type", "car");
        body.put("brand", "ITBrand");
        body.put("model", "ITModel");
        body.put("year", 2025);
        body.put("doors", 2);
        body.put("fuel", "Petrol");

        ResponseEntity<String> post = rest.postForEntity("/api/vehicles/add", body, String.class);
        assertThat(post.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String id = post.getBody();
        assertThat(id).isNotBlank();

        // GET by id
        ResponseEntity<String> get = rest.getForEntity("/api/vehicles/" + id, String.class);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.OK);
        String bodyStr = get.getBody();
        assertThat(bodyStr).contains("ITBrand").contains("ITModel");
    }
}
