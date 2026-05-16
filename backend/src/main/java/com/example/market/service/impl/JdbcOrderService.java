package com.example.market.service.impl;

import com.example.market.service.OrderEventPublisher;
import com.example.market.service.OrderService;
import com.example.market.web.dto.CreateOrderRequest;
import com.example.market.web.dto.OrderResponse;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class JdbcOrderService implements OrderService {

    private final JdbcTemplate jdbcTemplate;
    private final ProductImageResolver productImageResolver;
    private final OrderEventPublisher orderEventPublisher;

    public JdbcOrderService(
        JdbcTemplate jdbcTemplate,
        ProductImageResolver productImageResolver,
        OrderEventPublisher orderEventPublisher
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.productImageResolver = productImageResolver;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        String orderNo = nextOrderNo();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement("""
                INSERT INTO trade_order (order_no, product_id, buyer_id, seller_id, amount, status, buyer_remark)
                VALUES (?, ?, ?, 0, 0.00, 'PENDING', ?)
                """, new String[] {"id"});
            ps.setString(1, orderNo);
            ps.setLong(2, request.productId());
            ps.setLong(3, request.buyerId());
            ps.setString(4, StringUtils.hasText(request.buyerRemark()) ? request.buyerRemark() : null);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("订单创建失败");
        }
        OrderResponse order = getOrder(key.longValue());
        orderEventPublisher.publishOrderCreated(order);
        return order;
    }

    @Override
    public List<OrderResponse> listBuyerOrders(Long buyerId) {
        return jdbcTemplate.query(orderSelectSql() + """
            WHERE o.buyer_id = ?
            ORDER BY o.created_at DESC
            """, (rs, rowNum) -> mapOrder(rs), buyerId);
    }

    OrderResponse getOrder(Long orderId) {
        return jdbcTemplate.queryForObject(orderSelectSql() + " WHERE o.id = ?", (rs, rowNum) -> mapOrder(rs), orderId);
    }

    OrderResponse getPendingOrderForBuyer(Long orderId, Long buyerId) {
        return jdbcTemplate.queryForObject(orderSelectSql() + """
            WHERE o.id = ? AND o.buyer_id = ? AND o.status = 'PENDING'
            """, (rs, rowNum) -> mapOrder(rs), orderId, buyerId);
    }

    OrderResponse getOrderByOrderNo(String orderNo) {
        return jdbcTemplate.queryForObject(orderSelectSql() + " WHERE o.order_no = ?", (rs, rowNum) -> mapOrder(rs), orderNo);
    }

    private String orderSelectSql() {
        return """
            SELECT
              o.id AS order_id,
              o.order_no,
              o.product_id,
              o.buyer_id,
              o.seller_id,
              o.amount,
              o.status,
              o.created_at,
              o.paid_at,
              p.title AS product_title,
              p.trade_place,
              u.username AS seller_username,
              (
                SELECT pi.image_url
                FROM product_image pi
                WHERE pi.product_id = p.id
                ORDER BY pi.sort_no ASC, pi.id ASC
                LIMIT 1
              ) AS cover_image
            FROM trade_order o
            JOIN product p ON p.id = o.product_id
            JOIN sys_user u ON u.id = o.seller_id
            """;
    }

    private OrderResponse mapOrder(java.sql.ResultSet rs) throws java.sql.SQLException {
        Long productId = rs.getLong("product_id");
        return new OrderResponse(
            rs.getLong("order_id"),
            rs.getString("order_no"),
            productId,
            rs.getString("product_title"),
            rs.getLong("buyer_id"),
            rs.getLong("seller_id"),
            rs.getString("seller_username"),
            rs.getBigDecimal("amount"),
            rs.getString("status"),
            rs.getString("trade_place"),
            productImageResolver.resolve(productId, rs.getString("cover_image")),
            toLocalDateTime(rs.getTimestamp("created_at")),
            toLocalDateTime(rs.getTimestamp("paid_at"))
        );
    }

    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private String nextOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int suffix = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "O" + time + suffix;
    }
}
