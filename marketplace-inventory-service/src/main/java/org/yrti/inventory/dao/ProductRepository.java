package org.yrti.inventory.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.inventory.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Product p " +
            "SET p.reservedQuantity = p.reservedQuantity + :reserveQty " +
            "WHERE p.id = :productId " +
            "AND (p.quantity - p.reservedQuantity) >= :reserveQty")
    int tryReserveProduct(@Param("productId") Long productId, @Param("reserveQty") int reserveQty);

}
