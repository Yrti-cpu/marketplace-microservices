package org.yrti.order.dto;

import lombok.Data;

@Data
public class ProductReserveRequest {
    private Long productId;
    private Integer quantity;
}
