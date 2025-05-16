package org.yrti.order.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.yrti.order.dto.UserResponse;

/**
 * Feign-клиент для взаимодействия с сервисом пользователей (USER-SERVICE). Предоставляет методы для
 * получения информации о пользователях и продавцах.
 */
@FeignClient(name = "USER-SERVICE")
public interface UserClient {

  /**
   * Получает информацию о пользователе по ID.
   *
   * @param userId ID пользователя
   * @return данные пользователя
   * @throws feign.FeignException в случае ошибки
   */
  @GetMapping("/api/users/{userId}")
  UserResponse getUserById(@PathVariable("userId") Long userId);

  /**
   * Пакетное получение email-адресов продавцов.
   *
   * @param userIds список ID пользователей
   * @return список email в том же порядке, что и userIds
   * @throws feign.FeignException в случае ошибки
   */
  @GetMapping("/api/users/{userIds}/sellers")
  List<String> getUsersBatch(@PathVariable List<Long> userIds);
}
