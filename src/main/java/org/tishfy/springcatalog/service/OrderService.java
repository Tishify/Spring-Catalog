package org.tishfy.springcatalog.service;


import org.springframework.data.crossstore.ChangeSetPersister;
import org.tishfy.springcatalog.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> findAll();

    Optional<Order> findById(Long id);

    Order create(Order order);

    Optional<Order> update(Long id, Order order) throws ChangeSetPersister.NotFoundException;

    void delete(Long id) throws ChangeSetPersister.NotFoundException;
}
