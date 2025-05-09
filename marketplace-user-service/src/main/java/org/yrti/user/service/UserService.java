package org.yrti.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.user.dao.UserRepository;
import org.yrti.user.dto.UserResponse;
import org.yrti.user.model.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

//  public User getCurrentUser() {
//    String email = SecurityContextHolder.getContext().getAuthentication().getName();
//    return userRepository.findByEmail(email)
//        .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
//  }

  public UserResponse getUserById(Long id) {
    log.debug("Запрос профиля клиента: userId={}", id);
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Клиент с id: " + id + " не найден"));
    return new UserResponse(id, user.getEmail(), user.getName());
  }
}
