package com.projectmanagementsystembackend.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagementsystembackend.config.JwtProvider;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.UserRepository;
import com.projectmanagementsystembackend.vo.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

@Component
@AllArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private UserRepository userRepository;

    @Value("${frontend.url:http://localhost:3000/dashboard}")
    private String frontEndUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oauthUser = oauthToken.getPrincipal();


        String email = oauthUser.getAttribute("email");

        User user = userRepository.findByEmail(email);
        if (user == null) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullName(oauthUser.getAttribute("name"));
            Random random = new Random();
            String generatedPassword = "OauthUser@" + random.nextInt(10000, 99999);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            newUser.setPassword(passwordEncoder.encode(generatedPassword));
            userRepository.save(newUser);
        }
        // generate your JWT
        String jwt = JwtProvider.generateTokenForOauth(email,authentication);

        // instead of JSON, return HTML + JS to store token
        String redirectUrl = frontEndUrl; // your React app page after login

        String htmlResponse = "<!DOCTYPE html>" +
                "<html><head><script>" +
                "localStorage.setItem('token', '" + jwt + "');" +
                "window.location.href='" + redirectUrl + "';" +
                "</script></head><body></body></html>";

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(htmlResponse);
        response.getWriter().flush();
    }
}
