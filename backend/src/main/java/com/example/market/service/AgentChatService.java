package com.example.market.service;

import com.example.market.web.dto.AgentChatRequest;
import com.example.market.web.dto.AgentChatResponse;

public interface AgentChatService {

    AgentChatResponse chat(AgentChatRequest request);
}
