package org.yrti.order.model;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Integer quantity;

    private BigDecimal originalPrice;      // Без скидки
    private BigDecimal price;              // С учётом скидки
    private BigDecimal totalPrice;         // price * quantity
    private BigDecimal discountPercentage; // Сколько % было скинуто

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
