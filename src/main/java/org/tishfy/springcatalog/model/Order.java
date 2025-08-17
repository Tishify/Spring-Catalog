package org.tishfy.springcatalog.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    //TODO add validation min, max, required
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    // TODO can be changed after security integration
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_user"))
    @NotNull(message = "User cannot be null")
    private User user;

    @Column(name = "adding_time", nullable = false)
    private LocalDateTime addingTime;

    @Column(name = "total_cost", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Total Cost cannot be null")
    @Positive
    private BigDecimal totalCost;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 32)
    @Setter(AccessLevel.NONE)
    private List<OrderItem> orderItems;

    @PrePersist
    protected void onCreate() {
        if (addingTime == null) {
            addingTime = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", user=" + user +
                ", addingTime=" + addingTime +
                ", totalCost=" + totalCost +
                '}';
    }
}
