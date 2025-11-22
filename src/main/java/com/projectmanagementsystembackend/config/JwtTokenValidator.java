package com.projectmanagementsystembackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JwtTokenValidator extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwt = null;

        // 1. First try Authorization header
        String header = request.getHeader(JwtConstant.JWT_HEADER);
        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
        }

        // 2. If not found, try cookies
        if (jwt == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    break;
                }
            }
        }

        if (jwt != null) {
            try {
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String email = String.valueOf(claims.get("email"));
                String authorities = String.valueOf(claims.get("authorities"));

                List<GrantedAuthority> auths =
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(email, null, auths);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // Return 401 Unauthorized with JSON response
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", 401);
                errorResponse.put("error", "Unauthorized");
                errorResponse.put("message", "Invalid or expired token. Please login again.");
                errorResponse.put("path", request.getRequestURI());
                errorResponse.put("redirectToLogin", true);

                ObjectMapper mapper = new ObjectMapper();
                response.getWriter().write(mapper.writeValueAsString(errorResponse));
                return; // Stop filter chain
            }
        }

        filterChain.doFilter(request, response);

    }


}
