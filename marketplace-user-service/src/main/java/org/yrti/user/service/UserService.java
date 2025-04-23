package org.yrti.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yrti.user.dao.UserRepository;
import org.yrti.user.dto.UserResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

//    public Optional<User> getUserById(Long id) {
//        return userRepository.findById(id);
//    }

    public UserResponse getUserById(Long id) {
        log.debug("Запрос профиля клиента: userId={}", id);
        // Заглушка
        return new UserResponse(id, "testuser" + id + "@mail.com", "Test User " + id);
    }
}
