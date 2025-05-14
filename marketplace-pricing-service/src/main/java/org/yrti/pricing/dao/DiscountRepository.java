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

  List<Discount> findByProductIdInAndIsActiveTrueAndStartDateBeforeAndEndDateAfter(List<Long> productIds,
      LocalDateTime startDate,
      LocalDateTime endDate);
}
