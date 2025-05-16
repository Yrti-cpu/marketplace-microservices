package org.yrti.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {

  @NotNull(message = "ID пользователя обязателен")
  private Long id;
  @NotNull(message = "email пользователя обязателен")
  private String email;
  @NotNull(message = "имя пользователя обязательно")
  private String name;
}
