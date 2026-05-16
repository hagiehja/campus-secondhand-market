package com.example.market.service;

import com.example.market.web.dto.CreateOrderRequest;
import com.example.market.web.dto.OrderResponse;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    List<OrderResponse> listBuyerOrders(Long buyerId);
}
