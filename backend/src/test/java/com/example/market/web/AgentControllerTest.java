package com.example.market.web;

import com.example.market.service.AgentChatService;
import com.example.market.web.dto.AgentChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgentController.class)
class AgentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentChatService agentChatService;

    @Test
    void chatReturnsCampusMarketAssistantAnswer() throws Exception {
        when(agentChatService.chat(argThat(request -> "怎么发布二手教材？".equals(request.message()))))
            .thenReturn(new AgentChatResponse("点击发布商品，填写教材名称、价格和交易地点。", "deepseek", "deepseek-chat"));

        mockMvc.perform(post("/api/agent/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "userId": 1,
                      "message": "怎么发布二手教材？"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.answer").value("点击发布商品，填写教材名称、价格和交易地点。"))
            .andExpect(jsonPath("$.data.provider").value("deepseek"))
            .andExpect(jsonPath("$.data.modelName").value("deepseek-chat"));
    }
}
