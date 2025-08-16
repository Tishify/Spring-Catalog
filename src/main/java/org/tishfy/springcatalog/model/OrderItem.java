package org.tishfy.springcatalog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(OrderItemId.class)
@Builder
public class OrderItem {
    //TODO add quantity, price
    @Id
    @ManyToOne()
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orderitem_order"), referencedColumnName = "order_id")
    @JsonIgnore
    private Order order;


    @Id
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_orderitem_item"), referencedColumnName = "item_id")
    private Item item;
}
