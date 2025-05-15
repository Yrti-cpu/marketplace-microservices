package org.yrti.inventory.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.inventory.dao.ProductRepository;
import org.yrti.inventory.dto.ProductActionRequest;
import org.yrti.inventory.dto.ProductDto;
import org.yrti.inventory.exception.NotEnoughStockException;
import org.yrti.inventory.exception.ProductNotFoundException;
import org.yrti.inventory.model.Product;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository repository;
  private final ObjectMapper objectMapper;

  @Transactional
  public void reserveBatch(List<ProductActionRequest> request) {
    log.debug("Резервируем товары: {} ", request);

    checkProductBatchExist(request);
    executeProductAction(request, repository::reserveProductsBatch, "резервирование товаров");
  }

  @Transactional
  public void releaseBatch(List<ProductActionRequest> request) {
    log.debug("Списываем товары со склада: {}", request);

    checkProductBatchExist(request);
    executeProductAction(request, repository::releaseProductsBatch, "отправка товаров");
  }

  @Transactional
  public void cancelReserveBatch(List<ProductActionRequest> request) {
    log.debug("Отменяем резерв товаров: {}", request);

    checkProductBatchExist(request);
    executeProductAction(request, repository::cancelReserveBatch, "отмена резерва");
  }

  private <T> void executeProductAction(List<T> requests,
      Consumer<String> dbFunction,
      String actionDescription) {
    try {
      String json = objectMapper.writeValueAsString(requests);
      dbFunction.accept(json);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Ошибка сериализации JSON", e);
    } catch (DataAccessException e) {
      throw new NotEnoughStockException(
          "Ошибка при операции: " + actionDescription + " — " + e.getMessage());
    }
  }

  public Product createProduct(ProductDto product) {
    log.debug("Создание товара: {}", product.getName());
    return repository.save(Product.builder()
        .name(product.getName())
        .description(product.getDescription())
        .quantity(product.getQuantity())
        .sellerId(product.getSellerId())
        .build());
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


  public Product updateProduct(Long id, ProductDto updated) {
    Product product = repository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));
    product.setName(updated.getName());
    product.setDescription(updated.getDescription());
    product.setQuantity(updated.getQuantity());
    product.setSellerId(updated.getSellerId());
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
    return repository.findSellerIdsByProductIds(productIds)
        .stream().distinct()
        .toList(); // Важно убрать дубликаты (разные товары могут продаваться одним и тем же продавцом
  }

  private void checkProductBatchExist(List<ProductActionRequest> request) {
    if (request == null || request.isEmpty()) {
      throw new IllegalArgumentException("Список товаров не может быть пустым");
    }

    for (ProductActionRequest r : request) {
      if (r.getQuantity() <= 0) {
        throw new IllegalArgumentException("Количество должно быть больше 0");
      }
    }
  }
}
