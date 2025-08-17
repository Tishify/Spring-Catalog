package org.tishfy.springcatalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    //TODO add validation min, max, required
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "item_name", nullable = false, length = 200)
    @Size(min = 4, max = 200, message = "Name should have between 4 and 200 characters")
    @NotNull(message = "Name cannot be null")
    private String itemName;

    @Column(name = "item_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Name cannot be null")
    private BigDecimal itemPrice;

    @Column(name = "item_description", columnDefinition = "TEXT")
    @NotNull(message = "Name cannot be null")
    @Size(min = 4, max = 20000, message = "Name should have between 4 and 20000 characters")
    private String itemDescription;


}
