package org.yrti.order.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
  @JsonBackReference
  private Order order;
}
