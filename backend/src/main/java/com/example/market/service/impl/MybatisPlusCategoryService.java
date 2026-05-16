package com.example.market.service.impl;

import com.example.market.persistence.mapper.CategoryMapper;
import com.example.market.persistence.row.CategoryListRow;
import com.example.market.service.CategoryService;
import com.example.market.web.dto.CategoryResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MybatisPlusCategoryService implements CategoryService {

    private final CategoryMapper categoryMapper;

    public MybatisPlusCategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryResponse> listCategories() {
        return categoryMapper.listCategoryRows().stream()
            .map(this::toResponse)
            .toList();
    }

    private CategoryResponse toResponse(CategoryListRow row) {
        return new CategoryResponse(row.getId(), row.getName(), row.getProductCount());
    }
}
