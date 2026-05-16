package com.example.market.web.dto;

import java.math.BigDecimal;

public record PaymentResponse(
    Long orderId,
    String orderNo,
    String paymentNo,
    BigDecimal amount,
    String status,
    String message
) {
}
