package org.yrti.pricing.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yrti.pricing.model.Price;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

  Optional<Price> findByProductId(Long productId);

  List<Price> findByProductIdIn(List<Long> productId);

  boolean existsByProductId(Long productId);

  void deleteByProductId(Long productId);
}
