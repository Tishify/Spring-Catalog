package org.tishfy.springcatalog.tests.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.tishfy.springcatalog.tests.model.Role;
import org.tishfy.springcatalog.tests.model.User;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "config.dir=src//test//resources//",
        "spring.config.location=classpath:test-application.yml"
})
@DirtiesContext
@Disabled
class UserControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;
    Role admin = Role.builder().roleId(1L).build();

    @Test
    void getUsers() {
        ResponseEntity<List<User>> response = restTemplate.exchange(
                "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    void getUser() {
        ResponseEntity<List<User>> response = restTemplate.exchange(
                "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
        User user = User.builder().name("Farcuad The Second").email("Json").role(admin).build();
        ResponseEntity<User> responsePost = createUserRequest(user);

        User postUser = responsePost.getBody();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/user" + postUser.getUserId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(204, response.getStatusCode().value());
    }

    private ResponseEntity<User> createUserRequest(User user) {
        HttpEntity<User> request = new HttpEntity<>(user);
        return restTemplate.exchange(
                "/users",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
    }

}