package com.example.market.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "不能为空")
    @Size(min = 3, max = 30, message = "长度必须为3到30位")
    String username,

    @NotBlank(message = "不能为空")
    @Size(min = 7, max = 11, message = "必须大于6位小于12位")
    String password,

    @NotBlank(message = "不能为空")
    @Size(min = 3, max = 50, message = "必须大于2位")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5A-Za-z]+$", message = "只能包含中文或英文")
    String realName,

    @NotBlank(message = "不能为空")
    @Size(max = 30, message = "不能超过30位")
    String studentNo,

    @Size(max = 20, message = "不能超过20位")
    String phone,

    @Email(message = "格式不正确")
    @Size(max = 100, message = "不能超过100位")
    String email
) {
}
