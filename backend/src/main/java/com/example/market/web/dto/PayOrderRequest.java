package com.example.market.web.dto;

import jakarta.validation.constraints.NotNull;

public record PayOrderRequest(
    @NotNull Long buyerId
) {
}
