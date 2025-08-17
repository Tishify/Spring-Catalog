package org.tishfy.springcatalog.tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tishfy.springcatalog.model.Image;

import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    private Long itemId;

    private String itemName;

    private BigDecimal itemPrice;

    private String itemDescription;

    private List<Image> itemImage;
}

