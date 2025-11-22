package com.projectmanagementsystembackend.config.health;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class RedisHealthIndicator implements HealthIndicator {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Health health() {
        try {
            // Try to ping Redis
            redissonClient.getNodesGroup().pingAll();
            return Health.up()
                    .withDetail("redis", "Available")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("redis", "Unavailable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}

