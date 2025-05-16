package org.yrti.pricing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.yrti.pricing.dto.PriceRequest;
import org.yrti.pricing.model.Price;
import org.yrti.pricing.service.PriceService;

@Tag(name = "Цены", description = "Управляет ценами на товары")
@RestController
@RequestMapping("/api/pricing/price")
@RequiredArgsConstructor
public class PriceController {

  private final PriceService priceService;

  @Operation(summary = "Получить цену")
  @GetMapping("/{productId}")
  public ResponseEntity<Price> getPrice(@PathVariable Long productId) {
    return ResponseEntity.ok(priceService.getPrice(productId));

  }

  @Operation(summary = "Создать цену")
  @PostMapping()
  public ResponseEntity<Price> createPrice(@Valid @RequestBody PriceRequest price) {
    return ResponseEntity.ok(priceService.createPrice(price));
  }

  @Operation(summary = "Обновить цену")
  @PutMapping("/{productId}")
  public ResponseEntity<Price> updatePrice(
      @PathVariable Long productId,
      @Valid @RequestBody PriceRequest price) {
    return ResponseEntity.ok(priceService.updatePrice(productId, price));
  }

  @Operation(summary = "Удалить цену")
  @DeleteMapping("/{productId}")
  public ResponseEntity<String> deletePrice(@PathVariable Long productId) {
    priceService.deletePrice(productId);
    return ResponseEntity.ok("Цена для товара с id:" + productId + "удалена");
  }
}
