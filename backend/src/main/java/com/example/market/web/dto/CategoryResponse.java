package com.example.market.web.dto;

public record CategoryResponse(
    Long id,
    String name,
    Integer productCount
) {
}
