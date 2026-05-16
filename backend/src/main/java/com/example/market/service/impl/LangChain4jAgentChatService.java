package com.example.market.service.impl;

import com.example.market.config.AgentProperties;
import com.example.market.service.AgentChatService;
import com.example.market.web.dto.AgentChatRequest;
import com.example.market.web.dto.AgentChatResponse;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LangChain4jAgentChatService implements AgentChatService {

    private final AgentProperties properties;
    private final ChatModel chatModel;

    public LangChain4jAgentChatService(AgentProperties properties) {
        this.properties = properties;
        this.chatModel = createChatModel(properties);
    }

    @Override
    public AgentChatResponse chat(AgentChatRequest request) {
        if (chatModel == null) {
            return new AgentChatResponse(
                "AI 助手还没有连接 DeepSeek，请在后端设置 DEEPSEEK_API_KEY 后重启服务。",
                "local",
                "not-configured"
            );
        }

        String answer = chatModel.chat(buildPrompt(request));
        return new AgentChatResponse(answer, "deepseek", properties.getModelName());
    }

    private ChatModel createChatModel(AgentProperties properties) {
        if (!properties.isEnabled() || !StringUtils.hasText(properties.getApiKey())) {
            return null;
        }

        return OpenAiChatModel.builder()
            .baseUrl(properties.getBaseUrl())
            .apiKey(properties.getApiKey())
            .modelName(properties.getModelName())
            .temperature(properties.getTemperature())
            .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()))
            .maxRetries(properties.getMaxRetries())
            .build();
    }

    private String buildPrompt(AgentChatRequest request) {
        return properties.getSystemPrompt()
            + "\n当前用户ID：" + (request.userId() == null ? "未登录" : request.userId())
            + "\n用户问题：" + request.message();
    }
}
