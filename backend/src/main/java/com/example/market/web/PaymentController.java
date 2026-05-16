package com.example.market.web;

import com.example.market.service.PaymentService;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/alipay/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String alipayNotify(@RequestParam Map<String, String> params) {
        return paymentService.handleAlipayNotify(params) ? "success" : "failure";
    }
}
