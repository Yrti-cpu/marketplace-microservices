package org.yrti.inventory.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.inventory.dao.ProductRepository;
import org.yrti.inventory.dto.ProductActionRequest;
import org.yrti.inventory.exception.NotEnoughStockException;
import org.yrti.inventory.exception.ProductNotFoundException;
import org.yrti.inventory.model.Product;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository repository;

  @Transactional
  public void reserveBatch(List<ProductActionRequest> request) {
    log.debug("Резервируем товары: {} ", request);
    if (request == null || request.isEmpty()) {
      throw new IllegalArgumentException("Список товаров не может быть пустым");
    }

    for (ProductActionRequest r : request) {
      if (r.getQuantity() <= 0) {
        throw new IllegalArgumentException("Количество должно быть больше 0");
      }
    }
    int[] results = repository.reserveProductsBatch(request);
    for (int i = 0; i < results.length; i++) {
      if (results[i] == 0) {
        throw new NotEnoughStockException(
            "Недостаточно запаса для товара с id=" + request.get(i).getProductId());
      }
    }

    for (ProductActionRequest r : request) {
      log.debug("Зарезервирован товар id={}, qty={}", r.getProductId(), r.getQuantity());
    }
  }

  @Transactional
  public void releaseBatch(List<ProductActionRequest> request) {
    log.debug("Списываем товары со склада: {}", request);

    if (request == null || request.isEmpty()) {
      throw new IllegalArgumentException("Список товаров не может быть пустым");
    }

    for (ProductActionRequest r : request) {
      if (r.getQuantity() <= 0) {
        throw new IllegalArgumentException("Количество должно быть больше 0");
      }
    }

    int[] results = repository.releaseProductsBatch(request);

    for (int i = 0; i < results.length; i++) {
      if (results[i] == 0) {
        throw new IllegalArgumentException(
            "Недостаточный резерв для товара id=" + request.get(i).getProductId());
      }
    }

    for (ProductActionRequest r : request) {
      log.debug("Товар отгружен id={}, qty={}", r.getProductId(), r.getQuantity());
    }
  }

  @Transactional
  public void cancelReserveBatch(List<ProductActionRequest> request) {
    log.debug("Отменяем резерв товаров: {}", request);

    if (request == null || request.isEmpty()) {
      throw new IllegalArgumentException("Список товаров не может быть пустым");
    }

    for (ProductActionRequest r : request) {
      if (r.getQuantity() <= 0) {
        throw new IllegalArgumentException("Количество должно быть больше 0");
      }
    }

    int[] results = repository.cancelReserveBatch(request);

    for (int i = 0; i < results.length; i++) {
      if (results[i] == 0) {
        throw new IllegalArgumentException(
            "Недостаточный резерв для отмены у товара id=" + request.get(i).getProductId());
      }
    }

    for (ProductActionRequest r : request) {
      log.debug("Отмена резерва: товар id={}, qty={}", r.getProductId(), r.getQuantity());
    }
  }

  public Product createProduct(Product product) {
    log.debug("Создание товара: {}", product.getName());
    return repository.save(product);
  }


  public Product getProduct(Long id) {
    log.debug("Получение товара по id: {}", id);
    return repository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }


  public List<Product> getAllProducts() {
    log.debug("Получение всех товаров");
    return repository.findAll();
  }


  public Product updateProduct(Long id, Product updated) {
    Product product = repository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
    product.setName(updated.getName());
    product.setDescription(updated.getDescription());
    product.setQuantity(updated.getQuantity());
    product.setSellerId(updated.getSellerId());
    product.setReservedQuantity(updated.getReservedQuantity());
    log.debug("Обновление товара: {} ", updated.getName());
    return repository.save(product);
  }


  public void deleteProduct(Long id) {
    log.debug("Удаление товара: {} ", id);
    repository.deleteById(id);
  }

  @Transactional
  public List<Long> getSellersId(List<Long> productIds) {
    log.debug("Получение sellerIds  товаров: {} ", productIds);
    if (productIds == null || productIds.isEmpty()) {
      return Collections.emptyList();
    }
    List<Long> sellerIds = repository.findSellerIdsByProductIds(productIds);
    return sellerIds.stream().distinct().toList();
  }
}
