package org.yrti.pricing.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yrti.pricing.dto.PricingResponse;
import org.yrti.pricing.service.PricingService;

/**
 * Контроллер для управления ценами после применения скидок. Скрыт из публичной документации
 * Swagger, так как предназначен исключительно для внутреннего взаимодействия между микросервисами.
 * Доступ к этим endpoint'ам должен осуществляться только через сервисный вызов (через Feign
 * Client).
 */
@Hidden
@Tag(name = "Цены", description = "Управляет ценами после применения скидки на товары")
@RestController
@RequestMapping("/api/pricing")
@RequiredArgsConstructor
public class PricingController {

  private final PricingService pricingService;


  @Operation(
      summary = "Получение цены",
      description = "Позволяет получить цену на товар"
  )
  @GetMapping("/{productId}")
  public PricingResponse getProductPrice(@PathVariable Long productId) {
    return pricingService.getPrice(productId);
  }

  @Operation(
      summary = "Получение цены",
      description = "Позволяет получить цену на несколько товаров"
  )
  @PostMapping("/batch")
  public List<PricingResponse> getProductPriceBatch(
      @RequestBody List<Long> productIds) {
    return pricingService.getPriceBatch(productIds);
  }
}
