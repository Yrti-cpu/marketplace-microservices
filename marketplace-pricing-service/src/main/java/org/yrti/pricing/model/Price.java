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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Сущность цены")
@Entity
@Table(name = "prices",
    indexes = @Index(name = "idx_price_product", columnList = "product_id"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Price {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "product_id", nullable = false)
  private Long productId;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

}
