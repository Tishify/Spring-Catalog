package org.tishfy.springcatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tishfy.springcatalog.model.OrderItem;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_OrderId(Long oderId);

    List<OrderItem> findByItem_ItemId(Long itemId);

    boolean existsByOrder_OrderIdAndItem_ItemId(Long orderId, Long itemId);

    void deleteByOrder_OrderIdAndItem_ItemId(Long orderId, Long itemId);
}
