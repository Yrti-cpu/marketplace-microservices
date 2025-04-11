package org.yrti.order.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.model.Order;
import org.yrti.order.model.OrderItem;
import org.yrti.order.dto.ProductReserveRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;


    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = Order.builder()
                .userId(request.getUserId())
                .build();

        List<OrderItem> items = request.getItems().stream()
                .map(i -> {
                    // Резервируем товар по каждому item
                    ProductReserveRequest reserveRequest = new ProductReserveRequest();
                    reserveRequest.setProductId(i.getProductId());
                    reserveRequest.setQuantity(i.getQuantity());
                    inventoryClient.reserveProduct(reserveRequest);

                    return OrderItem.builder()
                            .productId(i.getProductId())
                            .quantity(i.getQuantity())
                            .order(order)
                            .build();
                })
                .collect(Collectors.toList());

        order.setItems(items);
        return orderRepository.save(order);
    }
}
