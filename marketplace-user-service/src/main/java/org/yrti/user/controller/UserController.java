package org.yrti.user.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yrti.user.dto.UserRequest;
import org.yrti.user.dto.UserResponse;
import org.yrti.user.service.UserService;

@Tag(name = "Пользователи", description = "Управляет пользователями")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @Operation(
      summary = "Получение пользователя"
  )
  @GetMapping("/{id}")
  public UserResponse getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  @Hidden
  @Operation(
      summary = "Получение почт продавцов"
  )
  @GetMapping("/{userIds}/sellers")
  public List<String> getUsersBatch(@PathVariable List<Long> userIds) {
    return userService.getUsersBatch(userIds);
  }


  @Operation(summary = "Создать пользователя")
  @PostMapping()
  public ResponseEntity<UserResponse> createDiscount(@Valid @RequestBody UserRequest discount) {
    return ResponseEntity.ok(userService.createUser(discount));
  }


  @Operation(summary = "Удалить пользователя")
  @DeleteMapping("/{userId}")
  public ResponseEntity<String> deleteDiscount(@PathVariable Long userId) {
    userService.deleteUser(userId);
    return ResponseEntity.ok("Пользователья с id:" + userId + "удален");
  }

  @Operation(summary = "Обновить пароль")
  @PatchMapping("/{userId}")
  public ResponseEntity<String> deactivateDiscount(@PathVariable Long userId,
      @RequestBody String newPassword) {
    userService.updateUserPassword(userId, newPassword);
    return ResponseEntity.ok("Пароль для пользователя с id:" + userId + "изменен");
  }
}
