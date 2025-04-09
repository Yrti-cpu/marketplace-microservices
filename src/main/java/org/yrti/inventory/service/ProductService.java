package org.yrti.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yrti.inventory.DAO.ProductRepository;
import org.yrti.inventory.exception.InvalidArgumentException;
import org.yrti.inventory.exception.NotEnoughStockException;
import org.yrti.inventory.exception.ProductNotFoundException;
import org.yrti.inventory.model.Product;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public void reserveProduct(Long id, int reservedQuantity) {
        if (reservedQuantity <= 0) {
            throw new InvalidArgumentException("Quantity must be greater than 0");
        }
        int updated = repository.tryReserveProduct(id, reservedQuantity);
        if (updated == 0) {
            throw new NotEnoughStockException("Not enough stock to reserve product");
        }
    }

    public void releaseProduct(Long id, int reservedQuantity) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (reservedQuantity <= 0 || product.getReservedQuantity() < reservedQuantity) {
            throw new InvalidArgumentException("Invalid quantity to release");
        }

        product.setReservedQuantity(Math.max(0, product.getReservedQuantity() - reservedQuantity));
        repository.save(product);
    }

    public void decreaseStock(Long id, int reservedQuantity) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (reservedQuantity <= 0 || product.getReservedQuantity() < reservedQuantity) {
            throw new InvalidArgumentException("Invalid quantity to release");
        }

        product.setQuantity(product.getQuantity() - reservedQuantity);
        product.setReservedQuantity(product.getReservedQuantity() - reservedQuantity);
        repository.save(product);
    }
    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Product getProduct(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
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
        product.setReservedQuantity(updated.getReservedQuantity());
        return repository.save(product);
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }
}
