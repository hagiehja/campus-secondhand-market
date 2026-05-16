package com.example.market.web;

import com.example.market.service.OrderService;
import com.example.market.service.PaymentService;
import com.example.market.web.dto.OrderResponse;
import com.example.market.web.dto.PaymentResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private PaymentService paymentService;

    @Test
    void createOrderReturnsPendingOrder() throws Exception {
        when(orderService.createOrder(any())).thenReturn(sampleOrder("PENDING"));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "productId": 1,
                      "buyerId": 3,
                      "buyerRemark": "图书馆门口交易"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.orderNo").value("O202605120001"))
            .andExpect(jsonPath("$.data.status").value("PENDING"))
            .andExpect(jsonPath("$.data.productTitle").value("数据库系统概论教材"));
    }

    @Test
    void listBuyerOrdersReturnsOrders() throws Exception {
        when(orderService.listBuyerOrders(3L)).thenReturn(List.of(sampleOrder("PAID")));

        mockMvc.perform(get("/api/orders")
                .param("buyerId", "3")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].status").value("PAID"))
            .andExpect(jsonPath("$.data[0].amount").value(25.00));
    }

    @Test
    void mockPayReturnsPaymentResult() throws Exception {
        when(paymentService.mockPay(eq(9L), eq(3L))).thenReturn(
            new PaymentResponse(9L, "O202605120001", "PAY202605120001", new BigDecimal("0.01"), "SUCCESS", "模拟支付成功")
        );

        mockMvc.perform(post("/api/orders/9/pay/mock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "buyerId": 3
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.status").value("SUCCESS"))
            .andExpect(jsonPath("$.data.amount").value(0.01));
    }

    @Test
    void alipayPageReturnsHtmlForm() throws Exception {
        when(paymentService.createAlipayPage(eq(9L), eq(3L))).thenReturn("<form id=\"alipay-submit\"></form>");

        mockMvc.perform(get("/api/orders/9/pay/alipay")
                .param("buyerId", "3"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
            .andExpect(content().string("<form id=\"alipay-submit\"></form>"));
    }

    private OrderResponse sampleOrder(String status) {
        return new OrderResponse(
            9L,
            "O202605120001",
            1L,
            "数据库系统概论教材",
            3L,
            2L,
            "seller01",
            new BigDecimal("25.00"),
            status,
            "图书馆门口",
            "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=900&q=80",
            LocalDateTime.of(2026, 5, 12, 20, 30),
            null
        );
    }
}
