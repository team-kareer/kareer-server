package org.sopt.kareer.global.auth.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.login-code")
public record LoginCodeProperties(
        long ttlSeconds,
        String redisPrefix
) {

    public Duration ttlDuration() {
        return Duration.ofSeconds(ttlSeconds);
    }

    public String buildKey(String code) {
        return redisPrefix + code;
    }
}
