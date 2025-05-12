package org.yrti.inventory.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.inventory.dao.ProductRepository;
import org.yrti.inventory.exception.NotEnoughStockException;
import org.yrti.inventory.exception.ProductNotFoundException;
import org.yrti.inventory.model.Product;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository repository;

  public void reserveProduct(Long id, int reservedQuantity) {
    if (reservedQuantity <= 0) {
      throw new IllegalArgumentException("Количество должно быть  больше 0");
    }
    int updated = repository.tryReserveProduct(id, reservedQuantity);
    log.debug("Резервируем товар: id={}, запрошено={}", id, reservedQuantity);

    if (updated == 0) {
      throw new NotEnoughStockException("Недостаточное количество для резервирования товара");
    }
  }

  public void releaseProduct(Long id, int reservedQuantity) {
    Product product = repository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

    if (reservedQuantity <= 0 || product.getReservedQuantity() < reservedQuantity) {
      throw new IllegalArgumentException("Неверное количество резерва");
    }
    log.debug("Отправка товара со склада: id={}, запрошено={}", id, reservedQuantity);
    product.setReservedQuantity(Math.max(0, product.getReservedQuantity() - reservedQuantity));
    product.setQuantity(product.getQuantity() - reservedQuantity);
    repository.save(product);
  }

  public void decreaseStock(Long id, int reservedQuantity) {
    Product product = repository.findById(id)
        .orElseThrow(() -> new ProductNotFoundException(id));

    if (reservedQuantity <= 0 || product.getReservedQuantity() < reservedQuantity) {
      throw new IllegalArgumentException("Неверное количество резерва");
    }
    product.setReservedQuantity(product.getReservedQuantity() - reservedQuantity);
    log.debug("Отмена резерва товара id: {}, количество: {}", id, reservedQuantity);
    repository.save(product);
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
    Product product = getProduct(id);
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
  public Set<Long> getSellersId(List<Long> productIds) {
    log.debug("Получение sellerIds  товаров: {} ", productIds);
    if (productIds == null || productIds.isEmpty()) {
      return Collections.emptySet();
    }
    return repository.findSellerIdsByProductIds(productIds);
  }
}
