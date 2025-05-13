package org.yrti.order.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.yrti.order.dto.ProductReserveRequest;

@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

  @GetMapping("/api/products/{productIds}/sellers")
  List<Long> getSellersId(@PathVariable List<Long> productIds);

  @PostMapping("/api/products/reserve/batch")
  ResponseEntity<String> reserveProductsForOrder(@RequestBody List<ProductReserveRequest> requests);

  @PostMapping("/api/products/release/batch")
  ResponseEntity<String> releaseProductsForOrder(@RequestBody List<ProductReserveRequest> requests);

  @PostMapping("/api/products/decrease/batch")
  ResponseEntity<String> decreaseProductsForOrder(@RequestBody List<ProductReserveRequest> requests);

}
