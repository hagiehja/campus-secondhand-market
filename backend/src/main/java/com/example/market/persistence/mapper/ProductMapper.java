package com.example.market.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.market.persistence.entity.ProductEntity;
import com.example.market.persistence.row.ProductListRow;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface ProductMapper extends BaseMapper<ProductEntity> {

    @Select("""
        <script>
        SELECT COUNT(*)
        FROM product p
        JOIN category c ON c.id = p.category_id
        JOIN sys_user u ON u.id = p.seller_id
        WHERE p.status = 'ON_SALE' AND c.status = 1 AND u.status = 1
        <if test="categoryId != null">
          AND p.category_id = #{categoryId}
        </if>
        <if test="keyword != null and keyword != ''">
          AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR c.name LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        </script>
        """)
    Long countOnSale(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    @Select("""
        <script>
        SELECT
          p.id AS productId,
          p.title,
          p.price,
          p.condition_level AS conditionLevel,
          p.trade_place AS tradePlace,
          p.view_count AS viewCount,
          p.created_at AS createdAt,
          c.name AS categoryName,
          u.username AS sellerUsername,
          (
            SELECT pi.image_url
            FROM product_image pi
            WHERE pi.product_id = p.id
            ORDER BY pi.sort_no ASC, pi.id ASC
            LIMIT 1
          ) AS coverImage
        FROM product p
        JOIN category c ON c.id = p.category_id
        JOIN sys_user u ON u.id = p.seller_id
        WHERE p.status = 'ON_SALE' AND c.status = 1 AND u.status = 1
        <if test="categoryId != null">
          AND p.category_id = #{categoryId}
        </if>
        <if test="keyword != null and keyword != ''">
          AND (p.title LIKE CONCAT('%', #{keyword}, '%') OR c.name LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        ORDER BY p.created_at DESC
        LIMIT #{limit} OFFSET #{offset}
        </script>
        """)
    List<ProductListRow> listOnSale(
        @Param("categoryId") Long categoryId,
        @Param("keyword") String keyword,
        @Param("limit") Integer limit,
        @Param("offset") Long offset
    );
}
