package com.projectmanagementsystembackend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class CustomInterceptor implements HandlerInterceptor {
    // Implement methods from HandlerInterceptor as needed
    // For example, preHandle, postHandle, afterCompletion, etc.

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        //here we can write custom logic like if token is compromised then using path userId we can validate the request
        log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        return true; // Continue with the request
    }

    @Override
    public void postHandle(@NotNull HttpServletRequest request,
                           @NotNull HttpServletResponse response,
                           @NotNull Object handler, ModelAndView modelAndView) throws Exception {
        // Custom logic after the request is handled
        log.info("Request URL: {}", request.getRequestURL());
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler, Exception ex) throws Exception {
        log.info("Request completed: {} {}", request.getMethod(), request.getRequestURI());
    }
}
