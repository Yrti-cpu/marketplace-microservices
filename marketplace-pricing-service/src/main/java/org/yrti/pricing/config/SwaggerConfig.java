package org.yrti.pricing.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "Pricing Service API",
        description = "API для управления ценами и скидками на товары",
        version = "1.0",
        contact = @Contact(
            name = "Dotsenko Danila"
        )
    )
)
@Configuration
public class SwaggerConfig {

}
