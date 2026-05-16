package com.example.market.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.market.persistence.entity.TradeOrderEntity;
import com.example.market.persistence.row.OrderListRow;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TradeOrderMapper extends BaseMapper<TradeOrderEntity> {

    String ORDER_SELECT = """
        SELECT
          o.id AS orderId,
          o.order_no AS orderNo,
          o.product_id AS productId,
          o.buyer_id AS buyerId,
          o.seller_id AS sellerId,
          o.amount,
          o.status,
          o.created_at AS createdAt,
          o.paid_at AS paidAt,
          p.title AS productTitle,
          p.trade_place AS tradePlace,
          u.username AS sellerUsername,
          (
            SELECT pi.image_url
            FROM product_image pi
            WHERE pi.product_id = p.id
            ORDER BY pi.sort_no ASC, pi.id ASC
            LIMIT 1
          ) AS coverImage
        FROM trade_order o
        JOIN product p ON p.id = o.product_id
        JOIN sys_user u ON u.id = o.seller_id
        """;

    @Select(ORDER_SELECT + """
        WHERE o.buyer_id = #{buyerId}
        ORDER BY o.created_at DESC
        """)
    List<OrderListRow> listBuyerOrders(@Param("buyerId") Long buyerId);

    @Select(ORDER_SELECT + " WHERE o.id = #{orderId}")
    OrderListRow selectOrder(@Param("orderId") Long orderId);

    @Select(ORDER_SELECT + """
        WHERE o.id = #{orderId} AND o.buyer_id = #{buyerId} AND o.status = 'PENDING'
        """)
    OrderListRow selectPendingOrderForBuyer(@Param("orderId") Long orderId, @Param("buyerId") Long buyerId);

    @Select(ORDER_SELECT + " WHERE o.order_no = #{orderNo}")
    OrderListRow selectByOrderNo(@Param("orderNo") String orderNo);
}
