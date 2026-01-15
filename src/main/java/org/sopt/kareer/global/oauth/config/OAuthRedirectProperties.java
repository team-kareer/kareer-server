package org.sopt.kareer.global.oauth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.redirect")
public record OAuthRedirectProperties(
        String redirectUri
) {
}
