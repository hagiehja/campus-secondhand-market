package com.example.market.service.impl;

import com.example.market.service.OrderEventPublisher;
import com.example.market.web.dto.OrderResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(OrderEventPublisher.class)
public class NoopOrderEventPublisher implements OrderEventPublisher {

    @Override
    public void publishOrderCreated(OrderResponse order) {
        // 没有启用 RocketMQ 时保留空实现，不影响订单主流程。
    }
}
