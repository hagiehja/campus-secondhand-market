package com.example.market.persistence.mapper;

import java.math.BigDecimal;
import org.apache.ibatis.annotations.Select;

public interface MarketSummaryMapper {

    @Select("SELECT COUNT(*) FROM message WHERE is_read = 0")
    Integer countPendingChats();

    @Select("SELECT COUNT(*) FROM trade_order WHERE status IN ('PENDING', 'PAID')")
    Integer countActiveOrders();

    @Select("SELECT COUNT(*) FROM trade_order WHERE status = 'FINISHED'")
    Integer countFinishedOrders();

    @Select("SELECT COUNT(*) FROM product WHERE DATE(created_at) = CURDATE()")
    Integer countTodayNewProducts();

    @Select("SELECT COALESCE(SUM(price), 0) FROM product WHERE status = 'ON_SALE'")
    BigDecimal sumOnSaleProductPrice();
}
