package com.example.market.web.dto;

public record LoginResponse(
    String token,
    Long userId,
    String username,
    String role,
    String realName
) {
}
