package com.example.market.web;

import com.example.market.service.ProductService;
import com.example.market.web.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void listOnSaleProductsReturnsPagedProducts() throws Exception {
        ProductResponse product = new ProductResponse(
            1L,
            "数据库系统概论教材",
            "教材资料",
            "seller01",
            new BigDecimal("25.00"),
            "GOOD",
            "图书馆门口",
            "/uploads/products/db-book.jpg",
            12,
            LocalDateTime.of(2026, 5, 12, 10, 0)
        );
        when(productService.listOnSale(eq(1L), eq("数据库"), any(PageRequest.class)))
            .thenReturn(new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/products")
                .param("categoryId", "1")
                .param("keyword", "数据库")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.content[0].title").value("数据库系统概论教材"))
            .andExpect(jsonPath("$.data.content[0].categoryName").value("教材资料"))
            .andExpect(jsonPath("$.data.totalElements").value(1));
    }
}
