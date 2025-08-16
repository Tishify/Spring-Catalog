package org.tishfy.springcatalog.tests.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tishfy.springcatalog.model.Item;
import org.tishfy.springcatalog.model.Order;
import org.tishfy.springcatalog.model.OrderItemId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    private Item item;
}
