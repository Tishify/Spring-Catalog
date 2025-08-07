package org.tishfy.springcatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tishfy.springcatalog.model.Item;

import java.math.BigDecimal;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByItemNameContainingIgnoreCase(String itemName);

    List<Item> findByItemPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Item> findAllByOrderByItemName();

}
