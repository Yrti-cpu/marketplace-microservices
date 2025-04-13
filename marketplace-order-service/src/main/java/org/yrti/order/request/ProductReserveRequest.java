package org.yrti.order.request;

import lombok.Data;

@Data
public class ProductReserveRequest {
    private Long productId;
    private Integer quantity;
}
