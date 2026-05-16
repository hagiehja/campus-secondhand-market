package com.example.market.service;

import com.example.market.web.dto.LoginRequest;
import com.example.market.web.dto.LoginResponse;
import com.example.market.web.dto.PhoneLoginRequest;
import com.example.market.web.dto.RegisterRequest;
import com.example.market.web.dto.RegisterResponse;
import com.example.market.web.dto.SmsCodeRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    RegisterResponse register(RegisterRequest request);

    String sendSmsCode(SmsCodeRequest request);

    LoginResponse phoneLogin(PhoneLoginRequest request);
}
