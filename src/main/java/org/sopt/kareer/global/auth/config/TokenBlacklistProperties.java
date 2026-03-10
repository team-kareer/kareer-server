package org.sopt.kareer.global.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.token-blacklist")
public record TokenBlacklistProperties(
        String redisPrefix
) {

    public String buildKey(String token) {
        return redisPrefix + token;
    }
}
