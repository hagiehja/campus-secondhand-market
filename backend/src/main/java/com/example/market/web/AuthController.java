package com.example.market.web;

import com.example.market.service.AuthService;
import com.example.market.web.dto.LoginRequest;
import com.example.market.web.dto.LoginResponse;
import com.example.market.web.dto.PhoneLoginRequest;
import com.example.market.web.dto.RegisterRequest;
import com.example.market.web.dto.RegisterResponse;
import com.example.market.web.dto.SmsCodeRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PostMapping("/sms-code")
    public ApiResponse<String> sendSmsCode(@Valid @RequestBody SmsCodeRequest request) {
        return ApiResponse.ok(authService.sendSmsCode(request));
    }

    @PostMapping("/phone-login")
    public ApiResponse<LoginResponse> phoneLogin(@Valid @RequestBody PhoneLoginRequest request) {
        return ApiResponse.ok(authService.phoneLogin(request));
    }
}
