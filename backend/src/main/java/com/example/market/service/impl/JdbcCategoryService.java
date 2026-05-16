package com.example.market.service.impl;

import com.example.market.service.CategoryService;
import com.example.market.web.dto.CategoryResponse;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class JdbcCategoryService implements CategoryService {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCategoryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<CategoryResponse> listCategories() {
        return jdbcTemplate.query("""
            SELECT c.id, c.name, COUNT(p.id) AS product_count
            FROM category c
            LEFT JOIN product p ON p.category_id = c.id AND p.status = 'ON_SALE'
            WHERE c.status = 1
            GROUP BY c.id, c.name, c.sort_no
            ORDER BY c.sort_no ASC, c.id ASC
            """, (rs, rowNum) -> new CategoryResponse(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getInt("product_count")
        ));
    }
}
