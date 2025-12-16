package test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = main.SpringBootApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class VehicleControllerErrorIT {

    @Autowired
    private TestRestTemplate rest;

    @Test
    public void post_missingType_returnsBadRequest() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("brand", "X");
        body.put("model", "Y");
        body.put("year", 2020);

        ResponseEntity<Map> post = rest.postForEntity("/api/vehicles/add", body, Map.class);
        assertThat(post.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(post.getBody()).containsKey("error");
    }
}