package org.yrti.inventory.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yrti.inventory.dao.ProductRepository;
import org.yrti.inventory.exception.InvalidArgumentException;
import org.yrti.inventory.exception.NotEnoughStockException;
import org.yrti.inventory.model.Product;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void reserveProduct_shouldSucceed_whenStockAvailable() {
        //TODO-Крит хорошим тоном является прописывание этапов //Подготовка //Выполнение //Проверка
        Long productId = 1L;
        int quantity = 5;

        when(productRepository.tryReserveProduct(productId, quantity)).thenReturn(1);

        assertDoesNotThrow(() -> productService.reserveProduct(productId, quantity));
        verify(productRepository, times(1)).tryReserveProduct(productId, quantity);
    }

    @Test
    void reserveProduct_shouldThrow_whenNotEnoughStock() {

        Long productId = 1L;
        int quantity = 100;

        when(productRepository.tryReserveProduct(productId, quantity)).thenReturn(0);

        assertThrows(NotEnoughStockException.class, () ->
                productService.reserveProduct(productId, quantity));
    }

    @Test
    void reserveProduct_shouldThrow_whenQuantityIsZero() {
        Long productId = 1L;

        assertThrows(InvalidArgumentException.class, () ->
                productService.reserveProduct(productId, 0));
    }

    @Test
    void reserveProductShouldThrowWhenQuantityIsNegative() {
        Long productId = 1L;

        assertThrows(InvalidArgumentException.class, () ->
                productService.reserveProduct(productId, -5));
    }
    @Test
    void releaseProduct_shouldSucceed_whenValid() {
        Product product = Product.builder()
                .id(1L)
                .quantity(10)
                .reservedQuantity(5)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.releaseProduct(1L, 3);

        assertEquals(2, product.getReservedQuantity());
        verify(productRepository).save(product);
    }

    @Test
    void releaseProduct_shouldThrow_whenNotEnoughReserved() {
        Product product = Product.builder()
                .id(1L)
                .quantity(10)
                .reservedQuantity(2)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InvalidArgumentException.class, () ->
                productService.releaseProduct(1L, 5));
    }

    @Test
    void decreaseStock_shouldSucceed_whenValid() {
        Product product = Product.builder()
                .id(1L)
                .quantity(10)
                .reservedQuantity(5)
                .build();


        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.decreaseStock(1L, 3);

        assertEquals(7, product.getQuantity());
        assertEquals(2, product.getReservedQuantity());
        verify(productRepository).save(product);
    }

    @Test
    void decreaseStock_shouldThrow_whenTooMuch() {
        Product product = Product.builder()
                .id(1L)
                .quantity(10)
                .reservedQuantity(2)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InvalidArgumentException.class, () ->
                productService.decreaseStock(1L, 5));
    }
}