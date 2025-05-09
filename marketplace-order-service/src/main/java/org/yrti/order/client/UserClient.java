package org.yrti.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.yrti.order.dto.UserResponse;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

  @GetMapping("/api/users/{userId}")
  UserResponse getUserById(@PathVariable("userId") Long userId);
}
