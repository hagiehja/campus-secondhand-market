package com.example.market.service.impl;

import com.example.market.persistence.entity.TradeOrderEntity;
import com.example.market.persistence.mapper.TradeOrderMapper;
import com.example.market.persistence.row.OrderListRow;
import com.example.market.service.OrderEventPublisher;
import com.example.market.service.OrderService;
import com.example.market.web.dto.CreateOrderRequest;
import com.example.market.web.dto.OrderResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MybatisPlusOrderService implements OrderService {

    private final TradeOrderMapper orderMapper;
    private final ProductImageResolver productImageResolver;
    private final OrderEventPublisher orderEventPublisher;

    public MybatisPlusOrderService(
        TradeOrderMapper orderMapper,
        ProductImageResolver productImageResolver,
        OrderEventPublisher orderEventPublisher
    ) {
        this.orderMapper = orderMapper;
        this.productImageResolver = productImageResolver;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        TradeOrderEntity order = new TradeOrderEntity();
        order.setOrderNo(nextOrderNo());
        order.setProductId(request.productId());
        order.setBuyerId(request.buyerId());
        order.setSellerId(0L);
        order.setAmount(BigDecimal.ZERO);
        order.setStatus("PENDING");
        order.setBuyerRemark(StringUtils.hasText(request.buyerRemark()) ? request.buyerRemark() : null);
        orderMapper.insert(order);
        if (order.getId() == null) {
            throw new IllegalStateException("订单创建失败");
        }

        OrderResponse response = getOrder(order.getId());
        orderEventPublisher.publishOrderCreated(response);
        return response;
    }

    @Override
    public List<OrderResponse> listBuyerOrders(Long buyerId) {
        return orderMapper.listBuyerOrders(buyerId).stream()
            .map(this::toResponse)
            .toList();
    }

    OrderResponse getOrder(Long orderId) {
        return toResponse(required(orderMapper.selectOrder(orderId), "订单不存在"));
    }

    OrderResponse getPendingOrderForBuyer(Long orderId, Long buyerId) {
        return toResponse(required(orderMapper.selectPendingOrderForBuyer(orderId, buyerId), "订单不存在或已支付"));
    }

    OrderResponse getOrderByOrderNo(String orderNo) {
        return toResponse(required(orderMapper.selectByOrderNo(orderNo), "订单不存在"));
    }

    private OrderListRow required(OrderListRow row, String message) {
        if (row == null) {
            throw new IllegalArgumentException(message);
        }
        return row;
    }

    private OrderResponse toResponse(OrderListRow row) {
        return new OrderResponse(
            row.getOrderId(),
            row.getOrderNo(),
            row.getProductId(),
            row.getProductTitle(),
            row.getBuyerId(),
            row.getSellerId(),
            row.getSellerUsername(),
            row.getAmount(),
            row.getStatus(),
            row.getTradePlace(),
            productImageResolver.resolve(row.getProductId(), row.getCoverImage()),
            row.getCreatedAt(),
            row.getPaidAt()
        );
    }

    private String nextOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int suffix = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "O" + time + suffix;
    }
}
