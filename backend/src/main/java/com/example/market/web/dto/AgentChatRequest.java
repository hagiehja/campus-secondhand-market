package com.example.market.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgentChatRequest(
    Long userId,
    @NotBlank(message = "不能为空")
    @Size(max = 1000, message = "不能超过1000字")
    String message
) {
}
