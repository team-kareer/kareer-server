package org.sopt.kareer.global.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        TokenProperties accessToken,
        TokenProperties refreshToken
) {

    public record TokenProperties(
            long expirationSeconds
    ) {
    }
}
