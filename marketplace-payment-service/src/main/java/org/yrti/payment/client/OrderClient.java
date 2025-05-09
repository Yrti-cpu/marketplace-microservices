package org.yrti.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.yrti.payment.dto.OrderRequest;

@FeignClient(name = "ORDER-SERVICE")
public interface OrderClient {

  @GetMapping("/api/orders/{orderId}")
  OrderRequest getOrderById(@PathVariable Long orderId);
}