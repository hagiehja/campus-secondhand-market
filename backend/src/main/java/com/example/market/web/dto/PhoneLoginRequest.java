package com.example.market.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PhoneLoginRequest(
    @NotBlank(message = "不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "必须是有效的中国大陆手机号")
    String phone,

    @NotBlank(message = "不能为空")
    @Pattern(regexp = "^\\d{6}$", message = "必须是6位数字验证码")
    String code
) {
}
