package com.example.market.web;

import com.example.market.service.OrderService;
import com.example.market.service.PaymentService;
import com.example.market.web.dto.CreateOrderRequest;
import com.example.market.web.dto.OrderResponse;
import com.example.market.web.dto.PayOrderRequest;
import com.example.market.web.dto.PaymentResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public OrderController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok(orderService.createOrder(request));
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> listBuyerOrders(@RequestParam Long buyerId) {
        return ApiResponse.ok(orderService.listBuyerOrders(buyerId));
    }

    @PostMapping("/{orderId}/pay/mock")
    public ApiResponse<PaymentResponse> mockPay(
        @PathVariable Long orderId,
        @Valid @RequestBody PayOrderRequest request
    ) {
        return ApiResponse.ok(paymentService.mockPay(orderId, request.buyerId()));
    }

    @GetMapping(value = "/{orderId}/pay/alipay", produces = MediaType.TEXT_HTML_VALUE)
    public String alipayPage(@PathVariable Long orderId, @RequestParam Long buyerId) {
        return paymentService.createAlipayPage(orderId, buyerId);
    }
}
