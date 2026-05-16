package com.example.market.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderResponse(
    Long orderId,
    String orderNo,
    Long productId,
    String productTitle,
    Long buyerId,
    Long sellerId,
    String sellerUsername,
    BigDecimal amount,
    String status,
    String tradePlace,
    String coverImage,
    LocalDateTime createdAt,
    LocalDateTime paidAt
) {
}
