package org.tishfy.springcatalog.service;


import org.springframework.data.crossstore.ChangeSetPersister;
import org.tishfy.springcatalog.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    List<Item> findAll();

    Optional<Item> findById(Long id);

    Item create(Item item);

    Optional<Item> update(Long id, Item item) throws ChangeSetPersister.NotFoundException;

    void delete(Long id) throws ChangeSetPersister.NotFoundException;
}
