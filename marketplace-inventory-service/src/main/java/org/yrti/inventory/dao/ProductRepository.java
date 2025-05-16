package org.yrti.inventory.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yrti.inventory.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query("SELECT p.sellerId FROM Product p WHERE p.id IN :productIds")
  List<Long> findSellerIdsByProductIds(@Param("productIds") List<Long> productIds);

  //Возвращаемое значение есть, но игнорируется
  @Query(value = "SELECT reserve_products(CAST(:json AS jsonb))", nativeQuery = true)
  Integer reserveProductsBatch(@Param("json") String json);


  @Query(value = "SELECT release_products(CAST(:json AS jsonb))", nativeQuery = true)
  Integer releaseProductsBatch(@Param("json") String json);

  @Query(value = "SELECT cancel_reserve_products(CAST(:json AS jsonb))", nativeQuery = true)
  Integer cancelReserveBatch(@Param("jsonb") String json);
}
