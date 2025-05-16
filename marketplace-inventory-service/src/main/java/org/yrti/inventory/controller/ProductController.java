package org.yrti.inventory.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.yrti.inventory.dto.ProductDto;
import org.yrti.inventory.model.Product;
import org.yrti.inventory.service.ProductService;

@Tag(name = "Товары", description = "Управляет товарами на складе маркетплейса")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @Hidden // Скрываем из публичной документации, так как это внутренний эндпоинт
  @Operation(
      summary = "Резерв товаров",
      description = "Позволяет зарезервировать товары из заказа пользователя"
  )
  @PostMapping("/reserve/batch")
  public ResponseEntity<String> reserveBatch(
      @Valid @RequestBody List<ProductActionRequest> request) {
    productService.reserveBatch(request);
    return ResponseEntity.ok("Дата брони: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @Hidden // Скрываем из публичной документации, так как это внутренний эндпоинт
  @Operation(
      summary = "Отправка товаров",
      description = "Позволяет отправить товары из заказа пользователя со склада"
  )
  @PostMapping("/release/batch")
  public ResponseEntity<String> releaseBatch(
      @Valid @RequestBody List<ProductActionRequest> request) {
    productService.releaseBatch(request);
    return ResponseEntity.ok(
        "Дата выгрузки со склада: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @Hidden // Скрываем из публичной документации, так как это внутренний эндпоинт
  @Operation(
      summary = "Отмена резерва",
      description = "Позволяет отменить резерв товаров из заказа пользователя"
  )
  @PostMapping("/decrease/batch")
  public ResponseEntity<String> decreaseBatch(
      @Valid @RequestBody List<ProductActionRequest> request) {
    productService.cancelReserveBatch(request);
    return ResponseEntity.ok(
        "Дата отмены брони: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
  }

  @Operation(
      summary = "Создание товара",
      description = "Позволяет продавцу добавить товар"
  )
  @PostMapping
  public ResponseEntity<Product> create(@Valid @RequestBody ProductDto product) {
    return ResponseEntity.ok(productService.createProduct(product));
  }

  @Operation(
      summary = "Получение товара",
      description = "Позволяет получить информацию о товаре"
  )
  @GetMapping("/{id}")
  public ResponseEntity<Product> get(@PathVariable Long id) {
    return ResponseEntity.ok(productService.getProduct(id));
  }

  @Operation(
      summary = "Получение всех товаров",
      description = "Позволяет получить информацию о всех товарах"
  )
  @GetMapping
  public ResponseEntity<List<Product>> getAll() {
    return ResponseEntity.ok(productService.getAllProducts());
  }

  @Operation(
      summary = "Изменение товара",
      description = "Позволяет изменить информацию о товаре"
  )
  @PutMapping("/{id}")
  public ResponseEntity<Product> update(
      @PathVariable Long id,
      @Valid @RequestBody ProductDto product) {
    return ResponseEntity.ok(productService.updateProduct(id, product));
  }

  @Operation(
      summary = "Удаление товара",
      description = "Позволяет удалить информацию о товаре"
  )
  @DeleteMapping("/{id}")
  public ResponseEntity<String> delete(@PathVariable Long id) {
    productService.deleteProduct(id);
    return ResponseEntity.ok("Товар id: " + id + "успешно удален: ");
  }

  @Hidden
  @Operation(
      summary = "Получение ID продавцов товаров",
      description = "Позволяет получить ID продавцов товаров из заказа пользователя"
  )
  @GetMapping("/{productIds}/sellers")
  public List<Long> getSellersId(@PathVariable List<Long> productIds) {
    return productService.getSellersId(productIds);
  }

}
