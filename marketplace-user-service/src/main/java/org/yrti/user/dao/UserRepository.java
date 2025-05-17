package org.yrti.user.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.yrti.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

  @Query("SELECT u.email FROM User u WHERE u.id IN :userIds")
  List<String> findSellerEmailByIds(@Param("userIds") List<Long> userIds);
}
