package com.example.market.service.impl;

import com.example.market.persistence.mapper.CategoryMapper;
import com.example.market.persistence.row.CategoryListRow;
import com.example.market.web.dto.CategoryResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MybatisPlusCategoryServiceTest {

    @Test
    void listCategoriesMapsRowsFromMybatisPlusMapper() {
        CategoryMapper categoryMapper = mock(CategoryMapper.class);
        MybatisPlusCategoryService service = new MybatisPlusCategoryService(categoryMapper);
        CategoryListRow row = new CategoryListRow();
        row.setId(1L);
        row.setName("教材资料");
        row.setProductCount(8);

        when(categoryMapper.listCategoryRows()).thenReturn(List.of(row));

        List<CategoryResponse> categories = service.listCategories();

        assertEquals(1, categories.size());
        assertEquals("教材资料", categories.get(0).name());
        assertEquals(8, categories.get(0).productCount());
    }
}
