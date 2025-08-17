package org.tishfy.springcatalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.util.List;

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
    @Size(min = 4, max = 200, message = "Item Name should have between 4 and 200 characters")
    @NotNull(message = "Item Name cannot be null")
    private String itemName;

    @Column(name = "item_price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Item Price cannot be null")
    private BigDecimal itemPrice;

    @Column(name = "item_description", columnDefinition = "TEXT")
    @NotNull(message = "Item Description cannot be null")
    @Size(min = 4, max = 20000, message = "Item Description should have between 4 and 20000 characters")
    private String itemDescription;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 32)
    @Setter(AccessLevel.NONE)
    private List<Image> itemImage;
}
