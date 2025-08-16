package org.tishfy.springcatalog.tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    private Long itemId;

    private String itemName;

    private BigDecimal itemPrice;

    private String itemDescription;
}

