package org.yrti.inventory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.inventory.dao.ProductRepository;
import org.yrti.inventory.exception.InvalidArgumentException;
import org.yrti.inventory.exception.NotEnoughStockException;
import org.yrti.inventory.exception.ProductNotFoundException;
import org.yrti.inventory.model.Product;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;

    public void reserveProduct(Long id, int reservedQuantity) {
        if (reservedQuantity <= 0) {
            throw new InvalidArgumentException("Количество должно быть  больше 0");
        }
        int updated = repository.tryReserveProduct(id, reservedQuantity);
        log.info("Резервируем товар: id={}, запрошено={}", id, reservedQuantity); //TODO-минор достойна ли эта информация INFO уровня логирования ? Пользователи это не увидят, а логи в проде будут засираться. Возможно это должен быть уровень debug
        //TODO-минор аналогичное замечание насчет всех остальных логов
        if (updated == 0) {
            throw new NotEnoughStockException("Недостаточное количество для резервирования товара");
        }
    }

    public void releaseProduct(Long id, int reservedQuantity) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (reservedQuantity <= 0 || product.getReservedQuantity() < reservedQuantity) {
            throw new InvalidArgumentException("Неверное количество резерва");
        }

        product.setReservedQuantity(Math.max(0, product.getReservedQuantity() - reservedQuantity));
        repository.save(product);
    }

    public void decreaseStock(Long id, int reservedQuantity) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (reservedQuantity <= 0 || product.getReservedQuantity() < reservedQuantity) {
            throw new InvalidArgumentException("Неверное количество резерва");
        }

        product.setQuantity(product.getQuantity() - reservedQuantity);
        product.setReservedQuantity(product.getReservedQuantity() - reservedQuantity);
        log.info("Отмена резерва товара id: {}, количество: {}", id, reservedQuantity);
        repository.save(product);
    }
    public Product createProduct(Product product) {
        log.info("Создание товара: {}", product.getName());
        return repository.save(product);
    }

    public Product getProduct(Long id) {
        log.info("Получение товара по id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> getAllProducts() {
        log.info("Получение всех товаров");
        return repository.findAll();
    }

    public Product updateProduct(Long id, Product updated) {
        Product product = getProduct(id);
        product.setName(updated.getName());
        product.setDescription(updated.getDescription());
        product.setQuantity(updated.getQuantity());
        product.setSeller(updated.getSeller());
        product.setReservedQuantity(updated.getReservedQuantity());
        log.info("Обновление товара: {} ", updated.getName());
        return repository.save(product);
    }

    public void deleteProduct(Long id) {
        log.info("Удаление товара: {} ", id);
        repository.deleteById(id);
    }
}
