package com.example.market.service.impl;

import com.example.market.web.dto.OrderResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RocketMqOrderEventPublisherTest {

    @Test
    void publishOrderCreatedSendsOrderEventToConfiguredTopic() {
        RocketMQTemplate rocketMQTemplate = mock(RocketMQTemplate.class);
        RocketMqOrderEventPublisher publisher = new RocketMqOrderEventPublisher(rocketMQTemplate, "campus-market-order-topic");

        publisher.publishOrderCreated(sampleOrder());

        verify(rocketMQTemplate).convertAndSend(eq("campus-market-order-topic:ORDER_CREATED"), argThat((Map<?, ?> payload) -> {
            return "ORDER_CREATED".equals(payload.get("eventType"))
                && Long.valueOf(9L).equals(payload.get("orderId"))
                && "O202605120001".equals(payload.get("orderNo"))
                && "PENDING".equals(payload.get("status"));
        }));
    }

    private OrderResponse sampleOrder() {
        return new OrderResponse(
            9L,
            "O202605120001",
            1L,
            "数据库系统概论教材",
            3L,
            2L,
            "seller01",
            new BigDecimal("25.00"),
            "PENDING",
            "图书馆门口",
            "https://example.com/book.jpg",
            LocalDateTime.of(2026, 5, 12, 20, 30),
            null
        );
    }
}
