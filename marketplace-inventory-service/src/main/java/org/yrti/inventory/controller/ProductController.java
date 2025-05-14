package org.yrti.inventory.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yrti.inventory.dto.ProductActionRequest;
import org.yrti.inventory.model.Product;
import org.yrti.inventory.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @PostMapping("/reserve/batch")
  public ResponseEntity<String> reserveBatch(
      @Valid @RequestBody List<ProductActionRequest> request) {
    productService.reserveBatch(request);
    return ResponseEntity.ok("Дата брони: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @PostMapping("/release/batch")
  public ResponseEntity<String> releaseBatch(
      @Valid @RequestBody List<ProductActionRequest> request) {
    productService.releaseBatch(request);
    return ResponseEntity.ok(
        "Дата выгрузки со склада: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @PostMapping("/decrease/batch")
  public ResponseEntity<String> decreaseBatch(
      @Valid @RequestBody List<ProductActionRequest> request) {
    productService.cancelReserveBatch(request);
    return ResponseEntity.ok(
        "Дата отмены брони: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @PostMapping
  public ResponseEntity<Product> create(@Valid @RequestBody Product product) {
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

  @GetMapping("/{productIds}/sellers")
  public List<Long> getSellersId(@PathVariable List<Long> productIds) {
    return productService.getSellersId(productIds);
  }

}
