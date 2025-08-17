package org.tishfy.springcatalog.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.tishfy.springcatalog.model.Order;
import org.tishfy.springcatalog.service.OrderService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderService service;

    @GetMapping
    public List<Order> getUsers() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Order> get(@PathVariable @Positive Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order create(@RequestBody @Valid Order order) {

        return service.create(order);
    }

    @PutMapping("/{id}")
    public Optional<Order> update(@PathVariable @Positive Long id, @RequestBody @Valid Order order) throws ChangeSetPersister.NotFoundException {
        return service.update(id, order);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) throws ChangeSetPersister.NotFoundException {
        service.delete(id);
    }
}
