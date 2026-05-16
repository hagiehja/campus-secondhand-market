package com.example.market.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.market.persistence.entity.CategoryEntity;
import com.example.market.persistence.row.CategoryListRow;
import java.util.List;
import org.apache.ibatis.annotations.Select;

public interface CategoryMapper extends BaseMapper<CategoryEntity> {

    @Select("""
        SELECT c.id, c.name, COUNT(p.id) AS productCount
        FROM category c
        LEFT JOIN product p ON p.category_id = c.id AND p.status = 'ON_SALE'
        WHERE c.status = 1
        GROUP BY c.id, c.name, c.sort_no
        ORDER BY c.sort_no ASC, c.id ASC
        """)
    List<CategoryListRow> listCategoryRows();
}
