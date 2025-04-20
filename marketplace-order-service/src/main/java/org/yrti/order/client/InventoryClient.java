package org.yrti.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.yrti.order.dto.ProductReserveRequest;

@FeignClient(name = "inventory-service", url = "${inventory-service.url}")
public interface InventoryClient {
    @PostMapping("/api/products/reserve")
    void reserveProduct(@RequestBody ProductReserveRequest request);

    @PostMapping("/api/products/release")
    void releaseProduct(@RequestBody ProductReserveRequest request);


}
