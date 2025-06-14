package com.projectmanagementsystembackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagementsystembackend.service.OtpService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test") // Use 'test' profile for testing
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OtpService otpService;

    @Test
    @Order(1)
    void testSignup() throws Exception {
        // Replace with your actual signup request fields
        var signupRequest = new SignupRequest("testuser", "test@example.com", "password123");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User Saved Successfully"));
    }

    @Test
    @Order(2)
    void testLogin() throws Exception {
        // Replace with your actual login request fields
        var loginRequest = new LoginRequest("test@example.com", "password123");
        var loginRequest2 = new LoginRequest("patelyash2462@gmail.com", "password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest2)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    @Order(3)
    void testLoginWithOtp() throws Exception {
        String email = "test@example.com";

        // 1. Send OTP
        mockMvc.perform(post("/auth/send-otp/{email}", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP sent successfully"));

        // 2. Retrieve OTP (replace with your OTPService or mock retrieval)
        String otp = otpService.generateOtp(email); // Or fetch from mock/service

        // 3. Login with OTP
        var loginRequest = new LoginRequest(email, null);
        // Add OTP field if your LoginRequest supports it
        loginRequest.otp = otp;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()));
    }

    // Define SignupRequest and LoginRequest as static classes or import your actual DTOs
    static class SignupRequest {
        public String username, email, password;
        public SignupRequest(String username, String email, String password) {
            this.username = username; this.email = email; this.password = password;
        }
    }
    static class LoginRequest {
        public String email, password,otp;
        public LoginRequest(String email, String password) {
            this.email = email; this.password = password;
        }
    }
}
