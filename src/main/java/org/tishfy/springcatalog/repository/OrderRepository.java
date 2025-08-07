package org.tishfy.springcatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tishfy.springcatalog.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser_UserId(Long userId);

    List<Order> findByTotalCostBetween(BigDecimal minCost, BigDecimal maxCost);

    List<Order> findByAddingTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<Order> findByUser_UserIdOrderByAddingTimeDesc(Long userId);

    List<Order> findAllByOrderByAddingTimeDesc();

    long countByUser_UserId(Long userId);
}
