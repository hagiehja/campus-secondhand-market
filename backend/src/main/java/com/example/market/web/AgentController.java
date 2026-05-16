package com.example.market.web;

import com.example.market.service.AgentChatService;
import com.example.market.web.dto.AgentChatRequest;
import com.example.market.web.dto.AgentChatResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentChatService agentChatService;

    public AgentController(AgentChatService agentChatService) {
        this.agentChatService = agentChatService;
    }

    @PostMapping("/chat")
    public ApiResponse<AgentChatResponse> chat(@Valid @RequestBody AgentChatRequest request) {
        return ApiResponse.ok(agentChatService.chat(request));
    }
}
