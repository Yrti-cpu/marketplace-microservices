package org.yrti.pricing.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Сущность скидки")
@Entity
@Table(name = "discounts",
    indexes = @Index(name = "idx_discount_product", columnList = "product_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(nullable = false, precision = 3, scale = 2)
  private BigDecimal discount;

  @Column(name = "start_date", nullable = false)
  private LocalDateTime startDate;

  @Column(name = "end_date")
  private LocalDateTime endDate;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = true; // Флаг для ручного управления

  public boolean isCurrentlyActive() {
    LocalDateTime now = LocalDateTime.now();
    return isActive
        && !now.isBefore(startDate)
        && (endDate == null || now.isBefore(endDate));
  }

}
