package com.example.market.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
    Long productId,
    String title,
    String categoryName,
    String sellerUsername,
    BigDecimal price,
    String conditionLevel,
    String tradePlace,
    String coverImage,
    Integer viewCount,
    LocalDateTime createdAt
) {
}
