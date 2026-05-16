package com.example.market.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "market.sms")
public class SmsProperties {

    private boolean enabled;
    private String accessKeyId;
    private String accessKeySecret;
    private String signName;
    private String templateCode;
    private String endpoint = "dysmsapi.aliyuncs.com";
    private int expireMinutes = 5;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getExpireMinutes() {
        return expireMinutes;
    }

    public void setExpireMinutes(int expireMinutes) {
        this.expireMinutes = expireMinutes;
    }
}
