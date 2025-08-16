package org.tishfy.springcatalog.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tishfy.springcatalog.model.Item;
import org.tishfy.springcatalog.service.ItemService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService service;

    @GetMapping
    public List<Item> getItems() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Item> get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestBody Item item) {

        return service.create(item);
    }

    @PutMapping("/{id}")
    public Optional<Item> update(@PathVariable Long id, @RequestBody Item item) throws ChangeSetPersister.NotFoundException {
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        service.delete(id);
    }
}
