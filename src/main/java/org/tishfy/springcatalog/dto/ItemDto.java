package org.tishfy.springcatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tishfy.springcatalog.model.Item;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private Long itemId;
    private String itemName;
    private BigDecimal itemPrice;
    private String itemDescription;
    private String itemPhoto;
    private String category;
    private Integer quantity;
    private boolean active;

    public static ItemDto fromEntity(Item item) {
        return ItemDto.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .itemPrice(item.getItemPrice())
                .itemDescription(item.getItemDescription())
                .itemPhoto(item.getItemPhoto())
                .category(item.getCategory())
                .quantity(item.getQuantity())
                .active(item.isActive())
                .build();
    }

    public Item toEntity() {
        return Item.builder()
                .itemId(this.itemId)
                .itemName(this.itemName)
                .itemPrice(this.itemPrice)
                .itemDescription(this.itemDescription)
                .itemPhoto(this.itemPhoto)
                .category(this.category)
                .quantity(this.quantity)
                .active(this.active)
                .build();
    }
}