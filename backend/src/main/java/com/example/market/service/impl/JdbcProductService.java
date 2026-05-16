package com.example.market.service.impl;

import com.example.market.service.ProductService;
import com.example.market.web.dto.ProductResponse;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class JdbcProductService implements ProductService {

    private final JdbcTemplate jdbcTemplate;
    private final ProductImageResolver productImageResolver;

    public JdbcProductService(JdbcTemplate jdbcTemplate, ProductImageResolver productImageResolver) {
        this.jdbcTemplate = jdbcTemplate;
        this.productImageResolver = productImageResolver;
    }

    @Override
    public Page<ProductResponse> listOnSale(Long categoryId, String keyword, PageRequest pageRequest) {
        StringBuilder where = new StringBuilder(" WHERE p.status = 'ON_SALE' AND c.status = 1 AND u.status = 1");
        List<Object> params = new ArrayList<>();

        if (categoryId != null) {
            where.append(" AND p.category_id = ?");
            params.add(categoryId);
        }
        if (StringUtils.hasText(keyword)) {
            where.append(" AND (p.title LIKE ? OR c.name LIKE ?)");
            String like = "%" + keyword.trim() + "%";
            params.add(like);
            params.add(like);
        }

        Long total = jdbcTemplate.queryForObject("""
            SELECT COUNT(*)
            FROM product p
            JOIN category c ON c.id = p.category_id
            JOIN sys_user u ON u.id = p.seller_id
            """ + where, Long.class, params.toArray());

        List<Object> queryParams = new ArrayList<>(params);
        queryParams.add(pageRequest.getPageSize());
        queryParams.add((long) pageRequest.getPageNumber() * pageRequest.getPageSize());

        List<ProductResponse> content = jdbcTemplate.query("""
            SELECT
              p.id AS product_id,
              p.title,
              p.price,
              p.condition_level,
              p.trade_place,
              p.view_count,
              p.created_at,
              c.name AS category_name,
              u.username AS seller_username,
              (
                SELECT pi.image_url
                FROM product_image pi
                WHERE pi.product_id = p.id
                ORDER BY pi.sort_no ASC, pi.id ASC
                LIMIT 1
              ) AS cover_image
            FROM product p
            JOIN category c ON c.id = p.category_id
            JOIN sys_user u ON u.id = p.seller_id
            """ + where + " ORDER BY p.created_at DESC LIMIT ? OFFSET ?",
            (rs, rowNum) -> new ProductResponse(
                rs.getLong("product_id"),
                rs.getString("title"),
                rs.getString("category_name"),
                rs.getString("seller_username"),
                rs.getBigDecimal("price"),
                rs.getString("condition_level"),
                rs.getString("trade_place"),
                productImageResolver.resolve(rs.getLong("product_id"), rs.getString("cover_image")),
                rs.getInt("view_count"),
                toLocalDateTime(rs.getTimestamp("created_at"))
            ),
            queryParams.toArray()
        );

        return new PageImpl<>(content, pageRequest, total == null ? 0 : total);
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
