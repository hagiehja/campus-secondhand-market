package com.example.market.service;

import com.example.market.web.dto.OrderResponse;

public interface OrderEventPublisher {

    void publishOrderCreated(OrderResponse order);
}
