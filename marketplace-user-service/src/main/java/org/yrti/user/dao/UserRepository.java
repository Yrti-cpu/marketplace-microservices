package org.yrti.user.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yrti.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);
}
