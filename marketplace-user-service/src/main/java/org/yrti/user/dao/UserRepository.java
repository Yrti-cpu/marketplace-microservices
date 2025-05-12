package org.yrti.user.dao;

import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yrti.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  @Query("SELECT u.email FROM User u WHERE u.id IN :userIds")
  Set<String> findSellerEmailByIds(@Param("userIds") Set<Long> userIds);
}
