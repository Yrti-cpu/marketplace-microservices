package org.yrti.pricing.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yrti.pricing.model.Discount;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

  Optional<Discount> findByProductId(Long productId);

  /**
   * Находит все активные скидки для указанных товаров, действующие в заданный период времени.
   *
   * @param productIds список идентификаторов товаров для поиска
   * @param startDate  начальная дата периода (включительно)
   * @param endDate    конечная дата периода (включительно)
   * @return список найденных активных скидок, удовлетворяющих критериям
   */
  List<Discount> findByProductIdInAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(
      List<Long> productIds,
      LocalDateTime startDate,
      LocalDateTime endDate);

  boolean existsByProductId(Long productId);

  void deleteByProductId(Long productId);
}
