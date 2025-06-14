package com.projectmanagementsystembackend.ratelimiter;

import com.projectmanagementsystembackend.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
public class EmailRateLimitAspect {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(emailRateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, EmailRateLimit emailRateLimit) throws Throwable {
        String email = null;
        // Try to get email from path variable or request parameter
        email = request.getParameter("email");
        if (email == null) {
            // Try to extract from path (e.g., /send-otp/{email})
            String uri = request.getRequestURI();
            String[] parts = uri.split("/");
            for (String part : parts) {
                if (part.contains("@")) {
                    email = part;
                    break;
                }
            }
        }
        if (email == null) {
            throw new IllegalArgumentException("Email not found in request for rate limiting.");
        }

        String key = "email_rate_limit:" + email;
        RRateLimiter limiter = redissonClient.getRateLimiter(key);
        limiter.trySetRate(RateType.OVERALL, emailRateLimit.permits(), Duration.ofSeconds(emailRateLimit.durationSeconds()));
        if (!limiter.tryAcquire()) {
            throw new RateLimitExceededException("EMAIL_MAX_LIMIT_EXCEEDED");
        }
        return joinPoint.proceed();
    }
}
