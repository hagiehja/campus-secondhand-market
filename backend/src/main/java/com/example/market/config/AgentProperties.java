package com.example.market.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "market.agent")
public class AgentProperties {

    private boolean enabled = true;
    private String apiKey = "";
    private String baseUrl = "https://api.deepseek.com/v1";
    private String modelName = "deepseek-chat";
    private double temperature = 0.4;
    private int timeoutSeconds = 60;
    private int maxRetries = 1;
    private String systemPrompt = """
        你是校园二手交易系统“校园集市”的界面小助手。
        你只能围绕登录、商品搜索、发布商品、下单、订单、支付、分类筛选和校园交易安全回答。
        回答要简洁、中文、可操作，必要时提醒用户先登录 buyer01 / 123456 体验演示。
        """;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
}
