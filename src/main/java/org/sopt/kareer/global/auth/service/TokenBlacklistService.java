package org.sopt.kareer.global.auth.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.auth.config.TokenBlacklistProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenBlacklistProperties tokenBlacklistProperties;

    public void register(String token, long ttlSeconds) {
        if (ttlSeconds <= 0) {
            return;
        }

        redisTemplate.opsForValue()
                .set(tokenBlacklistProperties.buildKey(token), token, Duration.ofSeconds(ttlSeconds));
    }

    public boolean contains(String token) {
        Boolean hasKey = redisTemplate.hasKey(tokenBlacklistProperties.buildKey(token));
        return Boolean.TRUE.equals(hasKey);
    }
}
