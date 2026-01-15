package org.sopt.kareer.global.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.oauth")
public record OAuthRedirectProperties(
        String redirectUri
) {
}
