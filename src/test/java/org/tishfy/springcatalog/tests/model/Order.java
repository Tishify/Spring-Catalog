package org.tishfy.springcatalog.tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private Long orderId;

    private User user;

    private LocalDateTime addingTime;

    private BigDecimal totalCost;

    private List<OrderItem> orderItems = new ArrayList<>();
}
