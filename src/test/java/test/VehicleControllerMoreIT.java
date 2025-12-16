package test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@SpringBootTest(classes = main.SpringBootApp.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class VehicleControllerMoreIT {

    @Autowired
    private TestRestTemplate rest;

    @Test
    public void updateAndGet_ok() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("type", "car");
        body.put("brand", "UpdBrand");
        body.put("model", "UpdModel");
        body.put("year", 2024);
        body.put("doors", 4);
        body.put("fuel", "Petrol");

        ResponseEntity<String> post = rest.postForEntity("/api/vehicles/add", body, String.class);
        assertThat(post.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String id = post.getBody();
        assertThat(id).isNotBlank();

        // update brand
        Map<String, Object> update = new HashMap<>();
        update.put("brand", "UpdBrand2");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(update, headers);
        ResponseEntity<String> put = rest.exchange("/api/vehicles/"+id, org.springframework.http.HttpMethod.PUT, entity, String.class);
        assertThat(put.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<String> get = rest.getForEntity("/api/vehicles/" + id, String.class);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(get.getBody()).contains("UpdBrand2");
    }

    @Test
    public void delete_ok() throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("type", "car");
        body.put("brand", "DelBrand");
        body.put("model", "DelModel");
        body.put("year", 2024);
        body.put("doors", 2);
        body.put("fuel", "Petrol");

        ResponseEntity<String> post = rest.postForEntity("/api/vehicles/add", body, String.class);
        assertThat(post.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String id = post.getBody();
        assertThat(id).isNotBlank();

        ResponseEntity<String> del = rest.exchange("/api/vehicles/"+id, org.springframework.http.HttpMethod.DELETE, null, String.class);
        assertThat(del.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> get = rest.getForEntity("/api/vehicles/" + id, String.class);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void filterByBrand_ok() throws Exception {
        Map<String, Object> a = new HashMap<>();
        a.put("type", "car"); a.put("brand","FilterX"); a.put("model","A"); a.put("year",2022); a.put("doors",4); a.put("fuel","Petrol");
        Map<String, Object> b = new HashMap<>();
        b.put("type", "car"); b.put("brand","FilterY"); b.put("model","B"); b.put("year",2021); b.put("doors",4); b.put("fuel","Petrol");
        rest.postForEntity("/api/vehicles/add", a, String.class);
        rest.postForEntity("/api/vehicles/add", b, String.class);

        ResponseEntity<String[]> res = rest.getForEntity("/api/vehicles?brand=FilterX", String[].class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        // should find at least one
        assertThat(res.getBody()).isNotNull();
    }
}
