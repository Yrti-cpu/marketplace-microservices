package org.yrti.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "User Service API",
        description = "API для управления пользователями",
        version = "1.0",
        contact = @Contact(
            name = "Dotsenko Danila"
        )
    )
)
@Configuration
public class SwaggerConfig {

}
