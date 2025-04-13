package org.yrti.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yrti.order.client.InventoryClient;
import org.yrti.order.dao.OrderRepository;
import org.yrti.order.dto.CreateOrderRequest;
import org.yrti.order.exception.InventoryServiceException;
import org.yrti.order.exception.OrderCreationException;
import org.yrti.order.model.Order;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private OrderService orderService;


    private CreateOrderRequest buildRequest() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);

        CreateOrderRequest.OrderItemRequest item = new CreateOrderRequest.OrderItemRequest();
        item.setProductId(100L);
        item.setQuantity(2);

        request.setItems(List.of(item));
        return request;
    }

    @Test
    @DisplayName("Success order")
    void shouldCreateOrderSuccessfully() {

        CreateOrderRequest request = buildRequest();
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Order result = orderService.createOrder(request);


        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        verify(inventoryClient, times(1)).reserveProduct(any());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Error creation order — InventoryServiceException")
    void shouldThrowInventoryException_whenReserveFails() {
        // given
        CreateOrderRequest request = buildRequest();
        doThrow(new RuntimeException("Mocked reserve failure")).when(inventoryClient).reserveProduct(any());

        // when & then
        InventoryServiceException exception = assertThrows(InventoryServiceException.class, () -> {
            orderService.createOrder(request);
        });

        assertTrue(exception.getMessage().contains("Failed to reserve product"));
    }

    @Test
    @DisplayName("Error save order — OrderCreationException")
    void shouldThrowOrderCreationException_whenSaveFails() {
        // given
        CreateOrderRequest request = buildRequest();
        when(orderRepository.save(any())).thenThrow(new RuntimeException("DB down"));

        // when & then
        OrderCreationException exception = assertThrows(OrderCreationException.class, () -> {
            orderService.createOrder(request);
        });

        assertTrue(exception.getMessage().contains("Failed to create order"));
    }
}