package org.yrti.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.yrti.inventory.dto.ProductActionRequest;
import org.yrti.inventory.model.Product;
import org.yrti.inventory.service.ProductService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
@Import(ProductControllerTest.TestConfig.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("unused")
    @Autowired
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/products/reserve - успешный резерв")
    void reserveProduct_shouldReturn200_whenValidRequest() throws Exception {
        //Подготовка
        ProductActionRequest request = new ProductActionRequest();
        request.setProductId(1L);
        request.setQuantity(5);

        // Проверка
        mockMvc.perform(post("/api/products/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Дата брони: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));
    }

    @Test
    @DisplayName("POST /api/products/reserve - 400 неприемлемое количество")
    void reserveProduct_shouldReturn400_whenInvalidQuantity() throws Exception {
        //Подготовка
        ProductActionRequest request = new ProductActionRequest();
        request.setProductId(1L);
        request.setQuantity(0);

        // Проверка
        mockMvc.perform(post("/api/products/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products - создание товара")
    void createProduct_shouldReturn200() throws Exception {
        //Подготовка
        Product productRequest = new Product();
        productRequest.setName("Test Product");
        productRequest.setQuantity(100);

        // Проверка
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/products/release - успешная отправка со склада")
    void releaserProduct_shouldReturn200_whenValidRequest() throws Exception {
        ProductActionRequest request = new ProductActionRequest();
        request.setProductId(1L);
        request.setQuantity(5);

        // Проверка
        mockMvc.perform(post("/api/products/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Дата выгрузки со склада: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

    }

    @Test
    @DisplayName("POST /api/products/decrease - успешная отмена")
    void decrease() throws Exception {
        ProductActionRequest request = new ProductActionRequest();
        request.setProductId(1L);
        request.setQuantity(5);

        // Проверка
        mockMvc.perform(post("/api/products/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Дата выгрузки со склада: " + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProductService productService() {
            return Mockito.mock(ProductService.class);
        }
    }


}