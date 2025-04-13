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
import org.yrti.inventory.request.ProductActionRequest;
import org.yrti.inventory.model.Product;
import org.yrti.inventory.service.ProductService;

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

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProductService productService() {
            return Mockito.mock(ProductService.class);
        }
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/products/reserve - reserve success")
    void reserveProduct_shouldReturn200_whenValidRequest() throws Exception {
        ProductActionRequest request = new ProductActionRequest();
        request.setProductId(1L);
        request.setQuantity(5);

        mockMvc.perform(post("/api/products/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Product reserved"));
    }

    @Test
    @DisplayName("POST /api/products/reserve - 400 unacceptable quantity")
    void reserveProduct_shouldReturn400_whenInvalidQuantity() throws Exception {
        ProductActionRequest request = new ProductActionRequest();
        request.setProductId(1L);
        request.setQuantity(0);

        mockMvc.perform(post("/api/products/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products - create product")
    void createProduct_shouldReturn200() throws Exception {
        Product productRequest = new Product();
        productRequest.setName("Test Product");
        productRequest.setQuantity(100);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk());
    }
  
}