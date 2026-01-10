package org.sopt.kareer.global.config.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        TokenProperties accessToken,
        TokenProperties refreshToken
) {

    public record TokenProperties(
            String secret,
            long expirationSeconds
    ) {
    }
}
