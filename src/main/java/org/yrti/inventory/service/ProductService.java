package org.yrti.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yrti.inventory.DAO.ProductRepository;
import org.yrti.inventory.model.Product;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Product getProduct(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product updateProduct(Long id, Product updated) {
        Product product = getProduct(id);
        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setQuantity(updated.getQuantity());
        product.setSeller(updated.getSeller());
        return repository.save(product);
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }
}
