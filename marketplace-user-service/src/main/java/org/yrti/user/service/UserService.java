package org.yrti.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yrti.user.dto.UserResponse;
import org.yrti.user.model.User;
import org.yrti.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

//    public Optional<User> getUserById(Long id) {
//        return userRepository.findById(id);
//    }

    public UserResponse getUserById(Long id) {
        // Заглушка 💡
        return new UserResponse(id, "testuser" + id + "@mail.com", "Test User " + id);
    }
}
