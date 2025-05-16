package org.yrti.payment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "Payment Service API",
        description = "API для управления оплаты заказа",
        version = "1.0",
        contact = @Contact(
            name = "Dotsenko Danila"
        )
    )
)
@Configuration
public class SwaggerConfig {

}
