package com.example.market.service;

import com.example.market.web.dto.PaymentResponse;
import java.util.Map;

public interface PaymentService {

    PaymentResponse mockPay(Long orderId, Long buyerId);

    String createAlipayPage(Long orderId, Long buyerId);

    boolean handleAlipayNotify(Map<String, String> params);
}
