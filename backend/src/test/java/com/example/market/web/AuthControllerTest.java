package com.example.market.web;

import com.example.market.service.AuthService;
import com.example.market.web.dto.LoginResponse;
import com.example.market.web.dto.RegisterResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void loginReturnsTokenAndUserInfo() throws Exception {
        when(authService.login(any())).thenReturn(new LoginResponse("token-1", 1L, "admin", "ADMIN", "系统管理员"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "username": "admin",
                      "password": "123456"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.token").value("token-1"))
            .andExpect(jsonPath("$.data.username").value("admin"))
            .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    void registerReturnsNewBuyerUserInfo() throws Exception {
        when(authService.register(any())).thenReturn(new RegisterResponse(8L, "newBuyer", "王小明", "BUYER"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "username": "newBuyer",
                      "password": "1234567",
                      "realName": "王小明",
                      "studentNo": "2415304999",
                      "phone": "13800009999",
                      "email": "newBuyer@example.com"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.userId").value(8))
            .andExpect(jsonPath("$.data.username").value("newBuyer"))
            .andExpect(jsonPath("$.data.realName").value("王小明"))
            .andExpect(jsonPath("$.data.role").value("BUYER"));
    }

    @Test
    void sendSmsCodeReturnsSuccess() throws Exception {
        when(authService.sendSmsCode(any())).thenReturn("验证码已发送");

        mockMvc.perform(post("/api/auth/sms-code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "phone": "13800008888"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value("验证码已发送"));
    }

    @Test
    void phoneLoginReturnsTokenAndAutoRegistersWhenPhoneIsNew() throws Exception {
        when(authService.phoneLogin(any())).thenReturn(new LoginResponse("phone-token", 19L, "m13800008888", "BUYER", "手机用户"));

        mockMvc.perform(post("/api/auth/phone-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "phone": "13800008888",
                      "code": "246810"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data.token").value("phone-token"))
            .andExpect(jsonPath("$.data.username").value("m13800008888"))
            .andExpect(jsonPath("$.data.realName").value("手机用户"));
    }

    @Test
    void registerRejectsPasswordNotBetweenSevenAndElevenCharacters() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "username": "badBuyer",
                      "password": "123456",
                      "realName": "王小明",
                      "studentNo": "2415304998"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void registerRejectsRealNameWithTwoOrFewerCharacters() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "username": "badBuyer",
                      "password": "1234567",
                      "realName": "王二",
                      "studentNo": "2415304997"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(400));
    }
}
