package org.yrti.inventory.DAO;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yrti.inventory.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
