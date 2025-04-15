package org.yrti.order.request;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String name;
}
