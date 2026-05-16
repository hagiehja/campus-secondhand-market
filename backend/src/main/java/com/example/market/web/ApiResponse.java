package com.example.market.web;

public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(500, message, null);
    }
}
