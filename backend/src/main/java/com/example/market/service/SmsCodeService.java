package com.example.market.service;

public interface SmsCodeService {

    void sendLoginCode(String phone);

    void verifyLoginCode(String phone, String code);
}
