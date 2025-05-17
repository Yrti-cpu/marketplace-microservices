package org.yrti.order.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.yrti.order.dto.ProductReserveRequest;

/**
 * Feign-клиент для взаимодействия с сервисом инвентаризации (INVENTORY-SERVICE). Предоставляет
 * методы для управления резервированием товаров.
 */
@FeignClient(name = "INVENTORY-SERVICE")
public interface InventoryClient {

  /**
   * Получает список идентификаторов продавцов для указанных товаров.
   *
   * @param productIds список ID товаров
   * @return список ID продавцов в том же порядке, что и productIds
   * @throws feign.FeignException в случае ошибки (400, 404, 500)
   */
  @GetMapping("/api/products/{productIds}/sellers")
  List<Long> getSellersId(@PathVariable List<Long> productIds);

  /**
   * Резервирует товары для заказа.
   *
   * @param requests список запросов на резервирование
   * @return String дату резерва
   * @throws feign.FeignException в случае ошибки резервирования
   */
  @PostMapping("/api/products/reserve/batch")
  String reserveProductsForOrder(@RequestBody List<ProductReserveRequest> requests);

  /**
   * Списывает товары со склада.
   *
   * @param requests список запросов на списание
   * @return String дату списания
   * @throws feign.FeignException в случае ошибки или недостатка товаров
   */
  @PostMapping("/api/products/release/batch")
  String releaseProductsForOrder(@RequestBody List<ProductReserveRequest> requests);

  /**
   * Отменяет резервирование товаров.
   *
   * @param requests список запросов на отмену резерва
   * @return String дату резервации
   * @throws feign.FeignException в случае ошибки
   */
  @PostMapping("/api/products/decrease/batch")
  String decreaseProductsForOrder(@RequestBody List<ProductReserveRequest> requests);

}
