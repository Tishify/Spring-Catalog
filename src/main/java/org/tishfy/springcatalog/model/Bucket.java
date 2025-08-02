package org.tishfy.springcatalog.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bucket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bucket {

    @EmbeddedId
    private BucketId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", foreignKey = @ForeignKey(name = "fk_bucket_item"))
    @MapsId("itemId")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_bucket_user"))
    @MapsId("userId")
    private User user;

    @Column(name = "adding_time", nullable = false, insertable = false, updatable = false)
    private LocalDateTime addingTime;

    @PrePersist
    private void onCreate() {
        if (id == null) id = new BucketId();
        if (id.getAddingTime() == null) {
            id.setAddingTime(LocalDateTime.now());
        }
    }
}
