package com.example.market.service.impl;

import com.example.market.service.MarketSummaryService;
import com.example.market.web.dto.MarketSummaryResponse;
import java.math.BigDecimal;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class JdbcMarketSummaryService implements MarketSummaryService {

    private final JdbcTemplate jdbcTemplate;

    public JdbcMarketSummaryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MarketSummaryResponse getSummary() {
        Integer pendingChats = queryInt("SELECT COUNT(*) FROM message WHERE is_read = 0");
        Integer activeOrders = queryInt("SELECT COUNT(*) FROM trade_order WHERE status IN ('PENDING', 'PAID')");
        Integer finishedOrders = queryInt("SELECT COUNT(*) FROM trade_order WHERE status = 'FINISHED'");
        Integer todayNewProducts = queryInt("SELECT COUNT(*) FROM product WHERE DATE(created_at) = CURDATE()");
        BigDecimal marketValue = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(price), 0) FROM product WHERE status = 'ON_SALE'",
            BigDecimal.class
        );
        return new MarketSummaryResponse(pendingChats, activeOrders, finishedOrders, todayNewProducts, marketValue);
    }

    private Integer queryInt(String sql) {
        Integer value = jdbcTemplate.queryForObject(sql, Integer.class);
        return value == null ? 0 : value;
    }
}
