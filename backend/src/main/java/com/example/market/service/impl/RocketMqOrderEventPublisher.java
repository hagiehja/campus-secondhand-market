package com.example.market.service.impl;

import com.example.market.service.OrderEventPublisher;
import com.example.market.web.dto.OrderResponse;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(RocketMQTemplate.class)
@ConditionalOnProperty(prefix = "market.rocketmq", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RocketMqOrderEventPublisher implements OrderEventPublisher {

    private final RocketMQTemplate rocketMQTemplate;
    private final String orderTopic;

    public RocketMqOrderEventPublisher(
        RocketMQTemplate rocketMQTemplate,
        @Value("${market.rocketmq.order-topic:campus-market-order-topic}") String orderTopic
    ) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.orderTopic = orderTopic;
    }

    @Override
    public void publishOrderCreated(OrderResponse order) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventType", "ORDER_CREATED");
        payload.put("orderId", order.orderId());
        payload.put("orderNo", order.orderNo());
        payload.put("productId", order.productId());
        payload.put("buyerId", order.buyerId());
        payload.put("sellerId", order.sellerId());
        payload.put("amount", order.amount());
        payload.put("status", order.status());
        payload.put("createdAt", order.createdAt());
        payload.put("sentAt", LocalDateTime.now());

        try {
            rocketMQTemplate.convertAndSend(orderTopic + ":ORDER_CREATED", payload);
        } catch (RuntimeException ignored) {
            // MQ 用于异步通知；课程演示时 RocketMQ 未启动不阻断下单主流程。
        }
    }
}
