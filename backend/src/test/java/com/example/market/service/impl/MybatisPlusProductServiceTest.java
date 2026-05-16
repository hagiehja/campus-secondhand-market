package com.example.market.service.impl;

import com.example.market.persistence.mapper.ProductMapper;
import com.example.market.persistence.row.ProductListRow;
import com.example.market.web.dto.ProductResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MybatisPlusProductServiceTest {

    @Test
    void listOnSaleUsesMybatisPlusMapperAndResolvesCoverImage() {
        ProductMapper productMapper = mock(ProductMapper.class);
        ProductImageResolver imageResolver = mock(ProductImageResolver.class);
        MybatisPlusProductService service = new MybatisPlusProductService(productMapper, imageResolver);
        ProductListRow row = new ProductListRow();
        row.setProductId(1L);
        row.setTitle("数据库系统概论教材");
        row.setCategoryName("教材资料");
        row.setSellerUsername("seller01");
        row.setPrice(new BigDecimal("25.00"));
        row.setConditionLevel("GOOD");
        row.setTradePlace("图书馆门口");
        row.setCoverImage("/images/db-book.jpg");
        row.setViewCount(12);
        row.setCreatedAt(LocalDateTime.of(2026, 5, 12, 10, 0));

        when(productMapper.countOnSale(1L, "数据库")).thenReturn(1L);
        when(productMapper.listOnSale(1L, "数据库", 10, 0L)).thenReturn(List.of(row));
        when(imageResolver.resolve(1L, "/images/db-book.jpg")).thenReturn("/resolved/db-book.jpg");

        Page<ProductResponse> page = service.listOnSale(1L, "数据库", PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        assertEquals("数据库系统概论教材", page.getContent().get(0).title());
        assertEquals("/resolved/db-book.jpg", page.getContent().get(0).coverImage());
    }
}
