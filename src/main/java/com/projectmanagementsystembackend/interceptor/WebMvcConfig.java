package com.projectmanagementsystembackend.interceptor;

import com.projectmanagementsystembackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CustomInterceptor(userRepository))
                .addPathPatterns("/**") // Specify the paths to intercept
                .excludePathPatterns("/api/public/**"); // Exclude specific paths if needed
    }
}
