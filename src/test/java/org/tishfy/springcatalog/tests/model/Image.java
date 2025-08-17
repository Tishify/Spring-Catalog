package org.tishfy.springcatalog.tests.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tishfy.springcatalog.model.Item;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {

    private Long imageId;
}