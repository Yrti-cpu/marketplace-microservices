package org.yrti.order.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.yrti.order.dto.PricingResponse;

@FeignClient(name = "PRICING-SERVICE")
public interface PricingClient {

  @PostMapping("/api/pricing/batch")
  ResponseEntity<List<PricingResponse>> getProductPriceBatch(@RequestBody List<Long> productIds);
}
