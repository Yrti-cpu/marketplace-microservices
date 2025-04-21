package org.yrti.order.model;


import jakarta.persistence.*;
import lombok.*;

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

    private Double originalPrice;      // Без скидки //TODO-Крит все бабосики в BigDecimal
    private Double price;              // С учётом скидки
    private Double totalPrice;         // price * quantity
    private Double discountPercentage; // Сколько % было скинуто

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
