package org.yrti.pricing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yrti.pricing.dto.DiscountRequest;
import org.yrti.pricing.model.Discount;
import org.yrti.pricing.service.DiscountService;

@Tag(name = "Скидки", description = "Управляет скидками на товары")
@RestController
@RequestMapping("/api/pricing/discount")
@RequiredArgsConstructor
public class DiscountController {

  private final DiscountService discountService;

  @Operation(summary = "Получить скидку")
  @GetMapping("/{productId}")
  public ResponseEntity<Discount> getPrice(@PathVariable Long productId) {
    return ResponseEntity.ok(discountService.getDiscount(productId));

  }

  @Operation(summary = "Создать скидку")
  @PostMapping()
  public ResponseEntity<Discount> createDiscount(@Valid @RequestBody DiscountRequest discount) {
    return ResponseEntity.ok(discountService.createDiscount(discount));
  }

  @Operation(summary = "Обновить скидку")
  @PutMapping("/{productId}")
  public ResponseEntity<Discount> updateDiscount(
      @PathVariable Long productId,
      @Valid @RequestBody DiscountRequest discount) {
    return ResponseEntity.ok(discountService.updateDiscount(productId, discount));
  }

  @Operation(summary = "Удалить скидку")
  @DeleteMapping("/{productId}")
  public ResponseEntity<String> deleteDiscount(@PathVariable Long productId) {
    discountService.deleteDiscount(productId);
    return ResponseEntity.ok("Скидка для товара с id:" + productId + "удалена");
  }

  @Operation(summary = "Деактивировать скидку")
  @PatchMapping("/{productId}")
  public ResponseEntity<String> deactivateDiscount(@PathVariable Long productId) {
    discountService.deactivateDiscount(productId);
    return ResponseEntity.ok("Скидка для товара с id:" + productId + "деактивирована");
  }


}
