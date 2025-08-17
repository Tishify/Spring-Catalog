package org.tishfy.springcatalog.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "item_images")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(cascade = CascadeType.DETACH, optional = false)
    @JoinColumn(name = "item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_itemimages_item"), referencedColumnName = "item_id")
    @JsonIgnore
    private Item item;

    @Column(name = "image", columnDefinition = "BYTEA")
    @JsonIgnore
    private byte[] image;

}
