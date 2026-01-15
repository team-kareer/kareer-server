package org.sopt.kareer.global.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.cookie.refresh-token")
public record RefreshTokenCookieProperties(
        String name
) {
}
