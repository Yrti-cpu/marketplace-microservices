package org.yrti.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserResponse {
    @NotNull(message = "User ID is required")
    private Long id;
    @NotNull(message = "User email is required")
    private String email;
    @NotNull(message = "User name is required")
    private String name;
}
