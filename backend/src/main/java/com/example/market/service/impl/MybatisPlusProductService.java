package com.example.market.service.impl;

import com.example.market.persistence.mapper.ProductMapper;
import com.example.market.persistence.row.ProductListRow;
import com.example.market.service.ProductService;
import com.example.market.web.dto.ProductResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MybatisPlusProductService implements ProductService {

    private final ProductMapper productMapper;
    private final ProductImageResolver productImageResolver;

    public MybatisPlusProductService(ProductMapper productMapper, ProductImageResolver productImageResolver) {
        this.productMapper = productMapper;
        this.productImageResolver = productImageResolver;
    }

    @Override
    public Page<ProductResponse> listOnSale(Long categoryId, String keyword, PageRequest pageRequest) {
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;
        Long total = productMapper.countOnSale(categoryId, normalizedKeyword);
        List<ProductListRow> rows = productMapper.listOnSale(
            categoryId,
            normalizedKeyword,
            pageRequest.getPageSize(),
            (long) pageRequest.getPageNumber() * pageRequest.getPageSize()
        );

        List<ProductResponse> content = rows.stream()
            .map(this::toResponse)
            .toList();
        return new PageImpl<>(content, pageRequest, total == null ? 0 : total);
    }

    private ProductResponse toResponse(ProductListRow row) {
        return new ProductResponse(
            row.getProductId(),
            row.getTitle(),
            row.getCategoryName(),
            row.getSellerUsername(),
            row.getPrice(),
            row.getConditionLevel(),
            row.getTradePlace(),
            productImageResolver.resolve(row.getProductId(), row.getCoverImage()),
            row.getViewCount(),
            row.getCreatedAt()
        );
    }
}
