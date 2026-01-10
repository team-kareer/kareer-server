package org.sopt.kareer.global.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private TokenProperties accessToken = new TokenProperties();
    private TokenProperties refreshToken = new TokenProperties();

    @Getter
    @Setter
    public static class TokenProperties {
        private String secret;
        private long expirationSeconds;
    }
}
