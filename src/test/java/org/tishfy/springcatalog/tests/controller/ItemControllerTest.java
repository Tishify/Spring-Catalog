package org.tishfy.springcatalog.tests.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.tishfy.springcatalog.tests.exptions.model.ErrorResponse;
import org.tishfy.springcatalog.tests.model.Image;
import org.tishfy.springcatalog.tests.model.Item;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    void getItemWithImageList() {

        ResponseEntity<Item> response = restTemplate.exchange(
                "/items/1",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Item item = response.getBody();
        assertNotNull(item);
        assertEquals(2, item.getItemImage().size());
        assertNotNull(item.getItemImage().getFirst().getImageId());
        assertEquals("item1", item.getItemName());
        assertEquals("description item1", item.getItemDescription());
        assertEquals(2, item.getItemPrice().intValue());
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
        assertEquals("Item Description cannot be null", errors.getErrors().stream()
                .filter(e -> "itemDescription".equals(e.getField()))
                .map(ErrorResponse.FieldError::getMessage)
                .findFirst().orElse("Error Not Found"));
        assertEquals("Item Name should have between 4 and 200 characters", errors.getErrors().stream()
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
        Item item = Item.builder().itemName("Felix").itemDescription("Stray Cat").itemPrice(BigDecimal.valueOf(12)).build();
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

    @Test
    void createImage() throws Exception {
        byte[] imageBytes = Files.readAllBytes(Path.of("src/test/resources/test.png"));

        ByteArrayResource fileAsResource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return "test.png";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileAsResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Image> response = restTemplate.postForEntity(
                  "/items/2/images", requestEntity, Image.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getImage() {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(
                 "/items/{itemId}/images/{imageId}",
                byte[].class,
                1L, 1L
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
        assertThat(response.getBody()).isNotEmpty();
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

}