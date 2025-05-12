package org.yrti.order.client;

import java.util.List;
import java.util.Set;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.yrti.order.dto.ProductReserveRequest;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

  @PostMapping("/api/products/reserve")
  void reserveProduct(@RequestBody ProductReserveRequest request);

  @PostMapping("/api/products/release")
  void releaseProduct(@RequestBody ProductReserveRequest request);

  @PostMapping("/api/products/decrease")
  void decreaseProduct(@RequestBody ProductReserveRequest request);

  @GetMapping("/api/products/{productIds}/sellers")
  Set<Long> getSellersId(@PathVariable List<Long> productIds);
}
