package org.yrti.inventory.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yrti.inventory.model.Product;
import org.yrti.inventory.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Data
    @RequiredArgsConstructor
    public static class ProductActionRequest {
        private Long productId;
        private Integer quantity;
    }
    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestBody ProductActionRequest request) {
        productService.reserveProduct(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Product reserved");

    }

    @PostMapping("/release")
    public ResponseEntity<?> release(@RequestBody ProductActionRequest request) {
        productService.releaseItem(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Резерв снят");
    }

    @PostMapping("/decrease")
    public ResponseEntity<?> decrease(@RequestBody ProductActionRequest request) {
        productService.decreaseStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok("Товар списан со склада");
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

}
