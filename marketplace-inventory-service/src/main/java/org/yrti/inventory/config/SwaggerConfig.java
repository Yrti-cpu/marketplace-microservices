package org.yrti.inventory.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "Inventory Service API",
        description = "API для управления товарами на складе маркетплейса.",
        version = "1.0",
        contact = @Contact(
            name = "Dotsenko Danila"
        )
    )
)
@Configuration
public class SwaggerConfig {

}
