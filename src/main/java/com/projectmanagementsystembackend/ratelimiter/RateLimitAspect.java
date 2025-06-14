package com.projectmanagementsystembackend.ratelimiter;

import com.projectmanagementsystembackend.exception.RateLimitExceededException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private HttpServletRequest request;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String clientIp = (forwardedFor != null && !forwardedFor.isEmpty())
                ? forwardedFor.split(",")[0].trim()
                : request.getRemoteAddr();
        String key = "rate_limit:" + clientIp + ":" + joinPoint.getSignature().toShortString();
        RRateLimiter limiter = redissonClient.getRateLimiter(key);
        limiter.trySetRate(
                RateType.OVERALL,
                rateLimit.permits(),
                Duration.ofSeconds(rateLimit.durationSeconds())
        );

        if (!limiter.tryAcquire()) {
            throw new RateLimitExceededException("MAX_LIMIT_EXCEEDED");
        }
        return joinPoint.proceed();
    }
}
