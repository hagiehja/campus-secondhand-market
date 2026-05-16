package com.example.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.market.config.AlipayProperties;
import com.example.market.persistence.entity.OperationLogEntity;
import com.example.market.persistence.entity.PaymentRecordEntity;
import com.example.market.persistence.entity.TradeOrderEntity;
import com.example.market.persistence.mapper.OperationLogMapper;
import com.example.market.persistence.mapper.PaymentRecordMapper;
import com.example.market.persistence.mapper.TradeOrderMapper;
import com.example.market.service.PaymentService;
import com.example.market.web.dto.OrderResponse;
import com.example.market.web.dto.PaymentResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class MybatisPlusPaymentService implements PaymentService {

    private final TradeOrderMapper orderMapper;
    private final PaymentRecordMapper paymentRecordMapper;
    private final OperationLogMapper operationLogMapper;
    private final MybatisPlusOrderService orderService;
    private final AlipayProperties alipayProperties;
    private final ObjectMapper objectMapper;

    public MybatisPlusPaymentService(
        TradeOrderMapper orderMapper,
        PaymentRecordMapper paymentRecordMapper,
        OperationLogMapper operationLogMapper,
        MybatisPlusOrderService orderService,
        AlipayProperties alipayProperties,
        ObjectMapper objectMapper
    ) {
        this.orderMapper = orderMapper;
        this.paymentRecordMapper = paymentRecordMapper;
        this.operationLogMapper = operationLogMapper;
        this.orderService = orderService;
        this.alipayProperties = alipayProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public PaymentResponse mockPay(Long orderId, Long buyerId) {
        OrderResponse order = orderService.getPendingOrderForBuyer(orderId, buyerId);
        String paymentNo = nextPaymentNo();
        BigDecimal payAmount = demoPayAmount();
        insertPayment(order.orderId(), paymentNo, "MOCK", payAmount, "SUCCESS", "MOCK-" + paymentNo);
        markOrderPaid(order.orderNo());
        return new PaymentResponse(order.orderId(), order.orderNo(), paymentNo, payAmount, "SUCCESS", "模拟支付成功");
    }

    @Override
    public String createAlipayPage(Long orderId, Long buyerId) {
        OrderResponse order = orderService.getPendingOrderForBuyer(orderId, buyerId);
        BigDecimal payAmount = demoPayAmount();
        String paymentNo = nextPaymentNo();
        insertPayment(order.orderId(), paymentNo, "ALIPAY", payAmount, "INIT", null);

        if (!alipayConfigured()) {
            return unconfiguredAlipayHtml(order, payAmount);
        }

        Map<String, Object> bizContent = new LinkedHashMap<>();
        bizContent.put("out_trade_no", order.orderNo());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        bizContent.put("total_amount", payAmount.toPlainString());
        bizContent.put("subject", "校园二手交易-" + order.productTitle());
        bizContent.put("body", "课程设计演示订单：" + order.orderNo());

        Map<String, String> params = new LinkedHashMap<>();
        params.put("app_id", alipayProperties.getAppId());
        params.put("method", "alipay.trade.page.pay");
        params.put("format", "JSON");
        params.put("charset", "UTF-8");
        params.put("sign_type", "RSA2");
        params.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        params.put("version", "1.0");
        params.put("notify_url", alipayProperties.getNotifyUrl());
        params.put("return_url", alipayProperties.getReturnUrl());
        params.put("biz_content", toJson(bizContent));
        params.put("sign", AlipaySigner.sign(params, alipayProperties.getPrivateKey()));

        return autoSubmitForm(alipayProperties.getGateway(), params);
    }

    @Override
    @Transactional
    public boolean handleAlipayNotify(Map<String, String> params) {
        if (!alipayConfigured() || !AlipaySigner.verify(params, alipayProperties.getAlipayPublicKey())) {
            return false;
        }

        String tradeStatus = params.get("trade_status");
        if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
            return true;
        }

        String orderNo = params.get("out_trade_no");
        String gatewayTradeNo = params.get("trade_no");
        OrderResponse order = orderService.getOrderByOrderNo(orderNo);
        insertPayment(order.orderId(), nextPaymentNo(), "ALIPAY", demoPayAmount(), "SUCCESS", gatewayTradeNo);
        markOrderPaid(orderNo);
        return true;
    }

    private void markOrderPaid(String orderNo) {
        int rows = orderMapper.update(new LambdaUpdateWrapper<TradeOrderEntity>()
            .eq(TradeOrderEntity::getOrderNo, orderNo)
            .eq(TradeOrderEntity::getStatus, "PENDING")
            .set(TradeOrderEntity::getStatus, "PAID")
            .set(TradeOrderEntity::getPaidAt, LocalDateTime.now()));
        if (rows == 0) {
            throw new IllegalArgumentException("订单不存在或已支付");
        }

        TradeOrderEntity order = orderMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<TradeOrderEntity>()
            .eq(TradeOrderEntity::getOrderNo, orderNo)
            .last("LIMIT 1"));
        if (order != null) {
            OperationLogEntity log = new OperationLogEntity();
            log.setBizType("ORDER");
            log.setBizId(order.getId());
            log.setAction("PAY");
            log.setDetail("订单支付成功，订单号: " + orderNo);
            operationLogMapper.insert(log);
        }
    }

    private void insertPayment(Long orderId, String paymentNo, String channel, BigDecimal amount, String status, String gatewayTradeNo) {
        PaymentRecordEntity payment = new PaymentRecordEntity();
        payment.setOrderId(orderId);
        payment.setPaymentNo(paymentNo);
        payment.setChannel(channel);
        payment.setAmount(amount);
        payment.setStatus(status);
        payment.setGatewayTradeNo(gatewayTradeNo);
        payment.setPaidAt("SUCCESS".equals(status) ? LocalDateTime.now() : null);
        paymentRecordMapper.insert(payment);
    }

    private boolean alipayConfigured() {
        return alipayProperties.isEnabled()
            && StringUtils.hasText(alipayProperties.getAppId())
            && StringUtils.hasText(alipayProperties.getPrivateKey())
            && StringUtils.hasText(alipayProperties.getAlipayPublicKey())
            && StringUtils.hasText(alipayProperties.getNotifyUrl());
    }

    private BigDecimal demoPayAmount() {
        return alipayProperties.getDemoPayAmount().setScale(2, RoundingMode.HALF_UP);
    }

    private String nextPaymentNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int suffix = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "PAY" + time + suffix;
    }

    private String toJson(Map<String, Object> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("支付宝业务参数生成失败", ex);
        }
    }

    private String autoSubmitForm(String gateway, Map<String, String> params) {
        StringBuilder builder = new StringBuilder("""
            <!doctype html>
            <html lang="zh-CN">
            <head><meta charset="UTF-8"><title>跳转支付宝支付</title></head>
            <body>
            <form id="alipay-submit" method="post" action="%s">
            """.formatted(escapeHtml(gateway)));
        params.forEach((key, value) -> builder
            .append("<input type=\"hidden\" name=\"")
            .append(escapeHtml(key))
            .append("\" value=\"")
            .append(escapeHtml(value))
            .append("\">\n"));
        builder.append("""
            </form>
            <script>document.getElementById('alipay-submit').submit();</script>
            <p>正在跳转支付宝支付，请稍候...</p>
            </body>
            </html>
            """);
        return builder.toString();
    }

    private String unconfiguredAlipayHtml(OrderResponse order, BigDecimal payAmount) {
        return """
            <!doctype html>
            <html lang="zh-CN">
            <head><meta charset="UTF-8"><title>支付宝未配置</title></head>
            <body style="font-family:Microsoft YaHei,Arial,sans-serif;padding:32px;line-height:1.8">
              <h2>支付宝参数未配置</h2>
              <p>订单号：%s</p>
              <p>演示支付金额：￥%s</p>
              <p>请在 <code>backend/src/main/resources/application.yml</code> 填入 app-id、应用私钥、支付宝公钥、notify-url，并把 enabled 改为 true。</p>
              <p>本地课程演示可以回到页面点击“模拟支付”。</p>
            </body>
            </html>
            """.formatted(escapeHtml(order.orderNo()), payAmount.toPlainString());
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
    }
}
