package org.yrti.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yrti.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
