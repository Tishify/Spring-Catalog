package org.tishfy.springcatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tishfy.springcatalog.model.OrderItem;
import org.tishfy.springcatalog.model.OrderItemId;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {

    List<OrderItem> findByOrder_OrderId(Long orderId);

    List<OrderItem> findByItem_ItemId(Long itemId);

    boolean existsByOrder_OrderIdAndItem_ItemId(Long orderId, Long itemId);

    void deleteByOrder_OrderIdAndItem_ItemId(Long orderId, Long itemId);
}