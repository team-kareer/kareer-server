package org.sopt.kareer.global.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.sopt.kareer.global.auth.config.TokenBlacklistProperties;
import org.sopt.kareer.global.exception.customexception.GlobalException;
import org.sopt.kareer.global.exception.errorcode.GlobalErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String BLACKLIST_VALUE = "BLACKLISTED";

    private final RedisTemplate<String, String> redisTemplate;
    private final TokenBlacklistProperties tokenBlacklistProperties;

    public void register(String token, long ttlSeconds) {
        if (ttlSeconds <= 0) {
            return;
        }

        String tokenHash = hash(token);
        redisTemplate.opsForValue()
                .set(tokenBlacklistProperties.buildKey(tokenHash), BLACKLIST_VALUE, Duration.ofSeconds(ttlSeconds));
    }

    public boolean contains(String token) {
        String fingerprint = hash(token);
        Boolean hasKey = redisTemplate.hasKey(tokenBlacklistProperties.buildKey(fingerprint));
        return Boolean.TRUE.equals(hasKey);
    }

    private String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR, "SHA-256 algorithm not available");
        }
    }
}
