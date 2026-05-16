package com.example.market.web.dto;

import java.math.BigDecimal;

public record MarketSummaryResponse(
    Integer pendingChats,
    Integer activeOrders,
    Integer finishedOrders,
    Integer todayNewProducts,
    BigDecimal marketValue
) {
}
