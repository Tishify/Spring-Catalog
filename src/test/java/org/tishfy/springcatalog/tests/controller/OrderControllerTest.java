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
import org.tishfy.springcatalog.model.Item;
import org.tishfy.springcatalog.tests.exptions.model.ErrorResponse;
import org.tishfy.springcatalog.tests.model.Order;
import org.tishfy.springcatalog.tests.model.OrderItem;
import org.tishfy.springcatalog.tests.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
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
class OrderControllerTest extends BaseAutoTestConfiguration {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getOrders() {
        ResponseEntity<List<Order>> response = restTemplate.exchange(
                "/orders",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    void getOrder() {
        Order order = Order.builder()
                .totalCost(BigDecimal.valueOf(90))
                .user(User.builder().userId(1L).build())
                .orderItems(new ArrayList<>())
                .build();

        order.getOrderItems().add(OrderItem.builder().item(Item.builder().itemId(1L).build()).build());

        Order createdOrder = createOrderRequest(order).getBody();

        ResponseEntity<Order> response = restTemplate.exchange(
                "/orders/" + createdOrder.getOrderId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getOrderNotFound() {
        ResponseEntity<Order> response = restTemplate.exchange(
                "/orders/1000",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getOrderId() {
        ResponseEntity<Order> response = restTemplate.exchange(
                "/orders/1",
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
        Order order = Order.builder()
                .totalCost(BigDecimal.valueOf(90))
                .user(User.builder().userId(1L).build())
                .orderItems(new ArrayList<>())
                .build();

        order.getOrderItems().add(OrderItem.builder().item(Item.builder().itemId(1L).build()).build());

        HttpEntity<Order> OrderEntity = new HttpEntity<>(order);
        ResponseEntity<Order> response = restTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                OrderEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        Order createdOrder = response.getBody();
        assertNotNull(createdOrder.getOrderId());
        assertNotNull(createdOrder.getOrderItems());
        assertEquals(1, createdOrder.getOrderItems().size());
        assertEquals(BigDecimal.valueOf(90), createdOrder.getTotalCost());
    }

    @Test
    void createValidationError() {
        Order order = Order.builder()
                .totalCost(BigDecimal.valueOf(-1))
                .orderItems(new ArrayList<>())
                .build();

        order.getOrderItems().add(OrderItem.builder().item(Item.builder().itemId(1L).build()).build());

        HttpEntity<Order> OrderEntity = new HttpEntity<>(order);
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                OrderEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        ErrorResponse errors = response.getBody();
        assertEquals("must be greater than 0", errors.getErrors().stream()
                .filter(e -> "totalCost".equals(e.getField()))
                .map(ErrorResponse.FieldError::getMessage)
                .findFirst().orElse("Error Not Found"));
        assertEquals("Name cannot be null", errors.getErrors().stream()
                .filter(e -> "user".equals(e.getField()))
                .map(ErrorResponse.FieldError::getMessage)
                .findFirst().orElse("Error Not Found"));
    }

    @Test
    void update() {
        //prepare test order with two items
        Order order = Order.builder()
                .totalCost(BigDecimal.valueOf(90))
                .user(User.builder().userId(1L).build())
                .orderItems(new ArrayList<>())
                .build();

        order.getOrderItems().add(OrderItem.builder().item(Item.builder().itemId(1L).build()).build());
        order.getOrderItems().add(OrderItem.builder().item(Item.builder().itemId(2L).build()).build());
        Order createdOrder = createOrderRequest(order).getBody();

        //prepare order for update. One item for delete, add two new items
        Order OrderForUpdate = Order.builder()
                .totalCost(BigDecimal.valueOf(100))
                .orderItems(new ArrayList<>())
                .build();
        OrderForUpdate.getOrderItems().add(OrderItem.builder().item(Item.builder().itemId(2L).build()).build());
        OrderForUpdate.getOrderItems().add(OrderItem.builder().item(Item.builder().itemId(3L).build()).build());
        OrderForUpdate.getOrderItems().add(OrderItem.builder().item(Item.builder().itemId(4L).build()).build());

        //execute update order
        HttpEntity<Order> OrderEntity = new HttpEntity<>(OrderForUpdate);
        ResponseEntity<Order> response = restTemplate.exchange(
                "/orders/" + createdOrder.getOrderId(),
                HttpMethod.PUT,
                OrderEntity,
                new ParameterizedTypeReference<>() {
                }
        );

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());

        Order updatedOrder = response.getBody();
        assertNotNull(updatedOrder.getOrderId());
        assertNotNull(updatedOrder.getOrderItems());
        assertEquals(3, updatedOrder.getOrderItems().size());
        assertEquals(BigDecimal.valueOf(100), updatedOrder.getTotalCost());
    }

    @Test
    void delete() {
        Order order = Order.builder()
                .totalCost(BigDecimal.valueOf(90))
                .user(User.builder().userId(1L).build())
                .orderItems(new ArrayList<>())
                .build();

        order.getOrderItems().add(OrderItem.builder().item(Item.builder().itemId(1L).build()).build());

        Order createdOrder = createOrderRequest(order).getBody();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/orders/" + createdOrder.getOrderId(),
                HttpMethod.DELETE,
                null,
                Void.class
        );

        assertEquals(204, response.getStatusCode().value());
    }


    private ResponseEntity<Order> createOrderRequest(Order order) {
        HttpEntity<Order> request = new HttpEntity<>(order);
        return restTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
    }

    private ResponseEntity<Map<String, String>> createOrderInvalidRequest(Order order) {
        HttpEntity<Order> request = new HttpEntity<>(order);
        return restTemplate.exchange(
                "/orders",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
    }
}