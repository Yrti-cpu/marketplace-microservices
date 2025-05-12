package org.yrti.user.service;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yrti.user.dao.UserRepository;
import org.yrti.user.dto.UserResponse;
import org.yrti.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public UserResponse getUserById(Long id) {
    log.debug("Запрос профиля клиента: userId={}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Клиент с id: " + id + " не найден"));
    return new UserResponse(id, user.getEmail(), user.getName());
  }

  @Transactional
  public List<String> getUsersBatch(List<Long> userIds) {
    log.debug("Запрос почт продавцов: {}", userIds);

    if (userIds == null || userIds.isEmpty()) {
      return Collections.emptyList();
    }
    return userRepository.findSellerEmailByIds(userIds);

  }
}
