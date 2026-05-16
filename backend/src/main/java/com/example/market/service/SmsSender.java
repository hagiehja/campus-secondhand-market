package com.example.market.service;

public interface SmsSender {

    void sendCode(String phone, String code);
}
