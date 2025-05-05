package org.yrti.inventory.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yrti.inventory.dao.ProductRepository;
import org.yrti.inventory.exception.NotEnoughStockException;
import org.yrti.inventory.model.Product;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductService productService;

  @Test
  @DisplayName("Резерв товара — успех при наличии достаточного количества")
  void reserveProduct_shouldSucceed_whenStockAvailable() {
    // Подготовка
    Long productId = 1L;
    int quantity = 5;
    // Выполнение
    when(productRepository.tryReserveProduct(productId, quantity)).thenReturn(1);
    // Проверка
    assertDoesNotThrow(() -> productService.reserveProduct(productId, quantity));
    verify(productRepository, times(1)).tryReserveProduct(productId, quantity);
  }

  @Test
  @DisplayName("Резерв товара — провал при отсутствии достаточного количества")
  void reserveProduct_shouldThrow_whenNotEnoughStock() {
    // Подготовка
    Long productId = 1L;
    int quantity = 100;

    // Выполнение
    when(productRepository.tryReserveProduct(productId, quantity)).thenReturn(0);

    // Проверка
    assertThrows(NotEnoughStockException.class, () ->
        productService.reserveProduct(productId, quantity));
  }

  @Test
  @DisplayName("Резерв товара — провал при нулевом запросе резерва")
  void reserveProduct_shouldThrow_whenQuantityIsZero() {
    // Подготовка
    Long productId = 1L;
    int quantity = 0;
    // Проверка
    assertThrows(IllegalArgumentException.class, () ->
        productService.reserveProduct(productId, quantity));
  }

  @Test
  @DisplayName("Резерв товара — провал при отрицательном запросе резерва")
  void reserveProductShouldThrowWhenQuantityIsNegative() {
    // Подготовка
    Long productId = 1L;
    int quantity = -1;

    // Проверка
    assertThrows(IllegalArgumentException.class, () ->
        productService.reserveProduct(productId, quantity));
  }

  @Test
  @DisplayName("Отправка товара — успех при наличии товара на складе")
  void releaseProduct_shouldSucceed_whenValid() {
    //Подготовка
    Product product = Product.builder()
        .id(1L)
        .quantity(10)
        .reservedQuantity(5)
        .build();

    // Выполнение
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    productService.releaseProduct(1L, 3);

    // Проверка
    assertEquals(2, product.getReservedQuantity());
    verify(productRepository).save(product);
  }

  @Test
  @DisplayName("Отправка товара — провал при отсутствии достаточного количества товара на складе")
  void releaseProduct_shouldThrow_whenNotEnoughReserved() {
    //Подготовка
    Product product = Product.builder()
        .id(1L)
        .quantity(10)
        .reservedQuantity(2)
        .build();
    // Выполнение
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    // Проверка
    assertThrows(IllegalArgumentException.class, () ->
        productService.releaseProduct(1L, 5));
  }

  @Test
  @DisplayName("Отмена резерва — успех при достаточном количестве резерва")
  void decreaseStock_shouldSucceed_whenValid() {
    //Подготовка
    Product product = Product.builder()
        .id(1L)
        .quantity(10)
        .reservedQuantity(5)
        .build();

    // Выполнение
    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    productService.decreaseStock(1L, 3);

    // Проверка
    assertEquals(7, product.getQuantity());
    assertEquals(2, product.getReservedQuantity());
    verify(productRepository).save(product);
  }

  @Test
  @DisplayName("Отмена резерва — провал при недостаточном количестве резерва")
  void decreaseStock_shouldThrow_whenTooMuch() {
    //Подготовка
    Product product = Product.builder()
        .id(1L)
        .quantity(10)
        .reservedQuantity(2)
        .build();

    // Выполнение
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

    // Проверка
    assertThrows(IllegalArgumentException.class, () ->
        productService.decreaseStock(1L, 5));
  }
}