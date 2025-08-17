package org.tishfy.springcatalog.tests.controller;

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
import org.tishfy.springcatalog.tests.exptions.model.ErrorResponse;
import org.tishfy.springcatalog.tests.model.Item;

import java.math.BigDecimal;
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
class ItemControllerTest extends BaseAutoTestConfiguration {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getItems() {
        ResponseEntity<List<Item>> response = restTemplate.exchange(
                "/items",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    void getItem() {
        Item item = Item.builder().itemName("Farcuad The Second").itemDescription("Json").itemPrice(BigDecimal.valueOf(12)).build();
        ResponseEntity<Item> responsePost = createItemRequest(item);

        Item postItem = responsePost.getBody();
        ResponseEntity<Item> response = restTemplate.exchange(
                "/items/" + postItem.getItemId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getItemNotFound() {
        ResponseEntity<Item> response = restTemplate.exchange(
                "/items/1000",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    void create() {
        Item item = Item.builder()
                .itemName("Felix")
                .itemPrice(BigDecimal.valueOf(12))
                .itemDescription("Stray Cat")
                .build();

        HttpEntity<Item> itemEntity = new HttpEntity<>(item);
        ResponseEntity<Item> response = restTemplate.exchange(
                "/items",
                HttpMethod.POST,
                itemEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());

        Item createdItem = response.getBody();
        assertEquals("Felix", createdItem.getItemName());
        assertEquals(BigDecimal.valueOf(12), createdItem.getItemPrice());
        assertEquals("Stray Cat", createdItem.getItemDescription());
    }

    @Test
    void createValidationError() {
        Item item = Item.builder()
                .itemName("Fel")
                .itemPrice(BigDecimal.valueOf(12))
                .build();

        HttpEntity<Item> itemEntity = new HttpEntity<>(item);
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/items",
                HttpMethod.POST,
                itemEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorResponse errors = response.getBody();
        assertEquals("Name cannot be null", errors.getErrors().stream()
                .filter(e -> "itemDescription".equals(e.getField()))
                .map(ErrorResponse.FieldError::getMessage)
                .findFirst().orElse("Error Not Found"));
        assertEquals("Name should have between 4 and 200 characters", errors.getErrors().stream()
                .filter(e -> "itemName".equals(e.getField()))
                .map(ErrorResponse.FieldError::getMessage)
                .findFirst().orElse("Error Not Found"));
    }

    @Test
    void update() {
        Item item = Item.builder().itemName("Farcuad The Second").itemDescription("Json").itemPrice(BigDecimal.valueOf(12)).build();
        Item createdItem = createItemRequest(item).getBody();
        Item itemForUpdate = Item.builder().itemName("Felix").itemDescription("Stray Cat").itemPrice(BigDecimal.valueOf(13)).build();

        HttpEntity<Item> itemEntity = new HttpEntity<>(itemForUpdate);
        ResponseEntity<Item> response = restTemplate.exchange(
                "/items/" + createdItem.getItemId(),
                HttpMethod.PUT,
                itemEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        Item updatedItem = response.getBody();
        assertEquals("Felix", updatedItem.getItemName());
        assertEquals(BigDecimal.valueOf(13), updatedItem.getItemPrice());
        assertEquals("Stray Cat", updatedItem.getItemDescription());
    }

    @Test
    void delete() {
        Item item = Item.builder().itemName("Farcuad The Second").itemDescription("Json").itemPrice(BigDecimal.valueOf(12)).build();
        ResponseEntity<Item> responsePost = createItemRequest(item);

        Item postItem = responsePost.getBody();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/items/" + postItem.getItemId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(204, response.getStatusCode().value());
    }


    private ResponseEntity<Item> createItemRequest(Item item) {
        HttpEntity<Item> request = new HttpEntity<>(item);
        return restTemplate.exchange(
                "/items",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    private ResponseEntity<Map<String, String>> createItemInvalidRequest(Item item) {
        HttpEntity<Item> request = new HttpEntity<>(item);
        return restTemplate.exchange(
                "/items",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}