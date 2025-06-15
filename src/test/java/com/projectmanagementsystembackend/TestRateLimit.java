package com.projectmanagementsystembackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use 'test' profile for testing
public class TestRateLimit {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setupUser() {
        if (!userRepository.existsByEmail("test1@example.com")) {
            User user = new User();
            user.setFullName("testuser");
            user.setEmail("test1@example.com");
            user.setPassword(passwordEncoder.encode("password123"));
            userRepository.save(user);
        }
    }

    @Test
    void testSendOtpRateLimit() throws Exception {
        String email = "ratelimit@example.com";
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/auth/send-otp")
                            .param("email", email)
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Forwarded-For", "127.0.0.2"))
                    .andExpect(status().isOk());
        }
        // 6th request should be rate limited
        mockMvc.perform(post("/auth/send-otp")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Forwarded-For", "127.0.0.2"))
                .andExpect(status().isTooManyRequests());
        //not expecting a specific message here because we have two rate limiters, one foremail and one for login
//                .andExpect(jsonPath("$.message").value("MAX_LIMIT_EXCEEDED"));
    }

    @Test
    void testLoginRateLimit() throws Exception {
        // Ensure the user exists for login tests
        var loginRequest = new LoginRequest("test1@example.com", "password123");
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest))
                            .header("X-Forwarded-For", "127.0.0.3"))// change IP to simulate different user otherwise login in authcontroller test fails
                    .andExpect(status().isOk());
        }
        // 11th request should be rate limited
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .header("X-Forwarded-For", "127.0.0.3")) // change IP to simulate different user otherwise login in authcontroller test fails
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message").value("MAX_LIMIT_EXCEEDED"));
    }

    static class LoginRequest {
        public String email, password;
        public LoginRequest(String email, String password) {
            this.email = email; this.password = password;
        }
    }
}
