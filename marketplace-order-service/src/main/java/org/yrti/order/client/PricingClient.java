package org.yrti.order.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.yrti.order.dto.PricingResponse;

/**
 * Feign-клиент для взаимодействия с сервисом ценообразования (PRICING-SERVICE).
 * Предоставляет методы для получения цен на товары.
 */
@FeignClient(name = "PRICING-SERVICE")
public interface PricingClient {

  /**
   * Пакетное получение цен для списка товаров.
   *
   * @param productIds список ID товаров (макс. 1000 элементов)
   * @return список цен в том же порядке, что и productIds
   * @throws feign.FeignException в случае ошибки (400, 404, 500)
   */
  @PostMapping("/api/pricing/batch")
  List<PricingResponse> getProductPriceBatch(@RequestBody List<Long> productIds);
}
