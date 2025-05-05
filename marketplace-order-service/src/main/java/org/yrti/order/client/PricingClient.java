package org.yrti.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.yrti.order.dto.PricingResponse;

@FeignClient(name = "PRICING-SERVICE")
public interface PricingClient {

  @GetMapping("/api/pricing/{productId}")
  PricingResponse getProductPrice(@PathVariable Long productId);
}
