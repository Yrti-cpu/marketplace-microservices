package org.yrti.order.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.model.Order;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderCreationService orderCreationService;
    private final OrderPaymentService orderPaymentService;
    private final OrderDispatchService orderDispatchService;
    private final OrderDeliveryService orderDeliveryService;
    private final OrderCancellationService orderCancellationService;

    public Order createOrder(CreateOrderRequest request) {
        return orderCreationService.createOrder(request);
    }

    public void markOrderAsPaid(Long orderId) {
        orderPaymentService.markOrderAsPaid(orderId);
    }

    public void dispatchOrder(Long orderId) {
        orderDispatchService.dispatchOrder(orderId);
    }

    public void markOrderAsDelivered(Long orderId) {
        orderDeliveryService.markOrderAsDelivered(orderId);
    }

    public void cancelOrder(Long orderId) {
        orderCancellationService.cancelOrder(orderId);
    }

}
