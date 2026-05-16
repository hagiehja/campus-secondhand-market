package com.example.market.config;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "market.alipay")
public class AlipayProperties {

    private boolean enabled;
    private String gateway = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private String appId = "";
    private String privateKey = "";
    private String alipayPublicKey = "";
    private String notifyUrl = "";
    private String returnUrl = "http://127.0.0.1:5173";
    private BigDecimal demoPayAmount = new BigDecimal("0.01");

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public BigDecimal getDemoPayAmount() {
        return demoPayAmount;
    }

    public void setDemoPayAmount(BigDecimal demoPayAmount) {
        this.demoPayAmount = demoPayAmount;
    }
}
