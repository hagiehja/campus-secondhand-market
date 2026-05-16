package com.example.market.web.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
    @NotNull Long productId,
    @NotNull Long buyerId,
    String buyerRemark
) {
}
