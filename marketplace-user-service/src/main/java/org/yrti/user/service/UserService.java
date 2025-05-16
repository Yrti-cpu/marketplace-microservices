package org.yrti.user.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.user.dao.UserRepository;
import org.yrti.user.dto.UserRequest;
import org.yrti.user.dto.UserResponse;
import org.yrti.user.exception.UserNotFoundException;
import org.yrti.user.model.Role;
import org.yrti.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  @Cacheable(value = "user:userCache",
      key = "{#root.methodName, #id.hashCode()}",
      unless = "#result == null")
  public UserResponse getUserById(Long id) {
    log.debug("Запрос профиля клиента: userId={}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
    return mapToResponse(user);
  }

  @Transactional
  public List<String> getUsersBatch(List<Long> userIds) {
    log.debug("Запрос почт продавцов: {}", userIds);

    if (userIds == null || userIds.isEmpty()) {
      return Collections.emptyList();
    }
    return userRepository.findSellerEmailByIds(userIds);

  }

  public UserResponse createUser(UserRequest userRequest) {

    User user = userRepository.save(User.builder()
        .name(userRequest.getName())
        .email(userRequest.getEmail())
        .password(userRequest.getPassword())
        .role(Role.CUSTOMER) // Чтобы получить другую роль продавца,
        .build());          // надо подать заявку (сначала реализовать jwt)

    return mapToResponse(user);
  }

  public void updateUserPassword(Long id, String newPassword) {
    User existing = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    existing.setPassword(newPassword);
    userRepository.save(existing);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  private UserResponse mapToResponse(User user) {
    return new UserResponse(user.getId(), user.getEmail(), user.getName());
  }
}
